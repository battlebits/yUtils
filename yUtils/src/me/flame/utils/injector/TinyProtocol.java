package me.flame.utils.injector;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketLoginInStart;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.ServerConnection;
import net.minecraft.util.com.mojang.authlib.GameProfile;
// These are not versioned, but they require CraftBukkit
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelInitializer;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 * Represents a very tiny alternative to ProtocolLib in 1.7.2.
 * <p>
 * It now supports intercepting packets during login and status ping (such as
 * OUT_SERVER_PING)!
 * 
 * @author Kristian
 */
@SuppressWarnings("static-method")
public abstract class TinyProtocol {
	private static final AtomicInteger ID = new AtomicInteger(0);

	private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
	private Listener listener;

	private Set<Channel> uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().<Channel, Boolean> makeMap());

	private List<NetworkManager> networkManagers;

	private List<Channel> serverChannels = Lists.newArrayList();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;

	private String handlerName;

	protected volatile boolean closed;
	protected Plugin plugin;

	public TinyProtocol(final Plugin plugin) {
		this.plugin = plugin;

		// Compute handler name
		this.handlerName = getHandlerName();

		// Prepare existing players
		registerBukkitEvents();

		try {
			registerChannelHandler();
			registerPlayers(plugin);
		} catch (IllegalArgumentException ex) {
			// Damn you, late bind
			plugin.getLogger().info("[TinyProtocol] Delaying server channel injection due to late bind.");
			
			// Damn you, late bind
			new BukkitRunnable() {
				@Override
				public void run() {
					registerChannelHandler();
					registerPlayers(plugin);
					plugin.getLogger().info("[TinyProtocol] Late bind injection successful.");
				}
			}.runTask(plugin);
		}
	}

	private void createServerChannelHandler() {
		// Handle connected channels
		endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					synchronized (networkManagers) {
						// Stop injecting channels
						if (closed)
							return;
						injectChannelInternal(channel);
					}
				} catch (Exception e) {
					plugin.getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
				}
			}
		};

		// This is executed before Minecraft's channel handler
		beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(endInitProtocol);
			}
		};

		serverChannelHandler = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				Channel channel = (Channel) msg;

				// Prepare to initialize ths channel
				channel.pipeline().addFirst(beginInitProtocol);
				ctx.fireChannelRead(msg);
			}
		};
	}

	private void registerBukkitEvents() {
		listener = new Listener() {
			@EventHandler(priority = EventPriority.LOWEST)
			public final void onPlayerLogin(PlayerLoginEvent e) {
				if (closed)
					return;
				Channel channel = getChannel(e.getPlayer());

				// Don't inject players that have been explicitly uninjected
				if (!uninjectedChannels.contains(channel)) {
					injectPlayer(e.getPlayer());
				}
			}

			@EventHandler
			public final void onPluginDisable(PluginDisableEvent e) {
				if (e.getPlugin().equals(plugin)) {
					close();
				}
			}
		};

		this.plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	@SuppressWarnings("unchecked")
	private void registerChannelHandler() {
		MinecraftServer mcServer = ((CraftServer)Bukkit.getServer()).getServer();
		ServerConnection serverConnection = mcServer.ai();
		try {
			Field listF = ServerConnection.class.getDeclaredField("f");
			listF.setAccessible(true);
			networkManagers = (List<NetworkManager>) listF.get(serverConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// We need to synchronize against this list
		createServerChannelHandler();
		
		List<Object> list = null;
		try {
			Field field = serverConnection.getClass().getDeclaredField("e");
			field.setAccessible(true);
			list = (List<Object>) field.get(serverConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Object item : list) {
			if (!ChannelFuture.class.isInstance(item))
				break;
			// Channel future that contains the server connection
			Channel serverChannel = ((ChannelFuture) item).channel();

			serverChannels.add(serverChannel);
			serverChannel.pipeline().addFirst(serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (serverChannelHandler == null)
			return;

		for (Channel serverChannel : serverChannels) {
			final ChannelPipeline pipeline = serverChannel.pipeline();

			// Remove channel handler
			serverChannel.eventLoop().execute(new Runnable() {
				public void run() {
					try {
						pipeline.remove(serverChannelHandler);
					} catch (NoSuchElementException e) {
						// That's fine
					}
				}
			});
		}
	}

	private void registerPlayers(Plugin plugin) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			injectPlayer(player);
		}
	}

	public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
		return packet;
	}

	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		return packet;
	}

	public void sendPacket(Player player, Object packet) {
		sendPacket(getChannel(player), packet);
	}

	public void sendPacket(Channel channel, Object packet) {
		channel.pipeline().writeAndFlush(packet);
	}

	public void receivePacket(Player player, Object packet) {
		receivePacket(getChannel(player), packet);
	}

	public void receivePacket(Channel channel, Object packet) {
		channel.pipeline().context("encoder").fireChannelRead(packet);
	}

	protected String getHandlerName() {
		return "tiny-" + plugin.getName() + "-" + ID.incrementAndGet();
	}

	public void injectPlayer(Player player) {
		injectChannelInternal(getChannel(player)).player = player;
	}

	public void injectChannel(Channel channel) {
		injectChannelInternal(channel);
	}

	private PacketInterceptor injectChannelInternal(Channel channel) {
		try {
			PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(handlerName);

			// Inject our packet interceptor
			if (interceptor == null) {
				interceptor = new PacketInterceptor();
				channel.pipeline().addBefore("packet_handler", handlerName, interceptor);
				uninjectedChannels.remove(channel);
			}
			return interceptor;
		} catch (IllegalArgumentException e) {
			// Try again
			return (PacketInterceptor) channel.pipeline().get(handlerName);
		}
	}

	public Channel getChannel(Player player) {
		Channel channel = channelLookup.get(player.getName());

		// Lookup channel again
		if (channel == null) {
			EntityPlayer entity = ((CraftPlayer) player).getHandle();
			PlayerConnection connection = entity.playerConnection;
			NetworkManager manager = connection.networkManager;
			try {
				Field field = NetworkManager.class.getDeclaredField("m");
				field.setAccessible(true);

				channelLookup.put(player.getName(), channel = (Channel) field.get(manager));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return channel;
	}

	public void uninjectPlayer(Player player) {
		uninjectChannel(getChannel(player));
	}

	public void uninjectChannel(final Channel channel) {
		// No need to guard against this if we're closing
		if (!closed) {
			uninjectedChannels.add(channel);
		}

		// See ChannelInjector in ProtocolLib, line 590
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				channel.pipeline().remove(handlerName);
			}
		});
	}

	public boolean hasInjected(Player player) {
		return hasInjected(getChannel(player));
	}

	public boolean hasInjected(Channel channel) {
		return channel.pipeline().get(handlerName) != null;
	}

	public final void close() {
		if (!closed) {
			closed = true;

			// Remove our handlers
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				uninjectPlayer(player);
			}
			// Clean up Bukkit
			HandlerList.unregisterAll(listener);
			unregisterChannelHandler();
		}
	}

	private final class PacketInterceptor extends ChannelDuplexHandler {
		// Updated by the login event
		public volatile Player player;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			// Intercept channel
			final Channel channel = ctx.channel();
			handleLoginStart(channel, msg);

			try {
				msg = onPacketInAsync(player, channel, msg);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
			}

			if (msg != null) {
				super.channelRead(ctx, msg);
			}
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			try {
				msg = onPacketOutAsync(player, ctx.channel(), msg);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
			}

			if (msg != null) {
				super.write(ctx, msg, promise);
			}
		}

		private void handleLoginStart(Channel channel, Object packet) {
			if (packet instanceof PacketLoginInStart) {
				try {
					Field getGameProfile = packet.getClass().getDeclaredField("a");
					getGameProfile.setAccessible(true);
					GameProfile profile = (GameProfile) getGameProfile.get(packet);
					channelLookup.put(profile.getName(), channel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}