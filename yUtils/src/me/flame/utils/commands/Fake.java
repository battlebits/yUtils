package me.flame.utils.commands;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.flame.utils.Main;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.utils.UUIDFetcher;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Fake implements CommandExecutor {
	private Map<UUID, FakePlayer> names;

	public Fake() {
		names = new HashMap<UUID, FakePlayer>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		final Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("fake")) {
			if (!Main.getPlugin().getPermissionManager().hasGroupPermission(player, Group.YOUTUBER)) {
				player.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
				return true;
			}
			if (args.length != 1) {
				player.sendMessage(ChatColor.RED + "Uso correto: /fake <nick>");
				return true;
			}
			final String[] argss = args;
			if (!validate(args[0])) {
				player.sendMessage(ChatColor.RED + "Parece que o nick escolhido possui caracteres inapropriados ou e muito grande");
				return true;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (names.containsKey(player.getUniqueId())) {
						FakePlayer originalName = names.get(player.getUniqueId());
						if (argss[0].equalsIgnoreCase(originalName.lastPlayerName)) {
							Main.getPlugin().getTagManager().removePlayerTag(player);
							changeNick(player, argss[0]);
							Main.getPlugin().getTagManager().addPlayerTag(player, getPlayerDefaultTag(player));
							player.sendMessage(ChatColor.YELLOW + "Seu nick voltou ao normal e voce recebeu novamente sua tag!");
							return;
						}
					}
					@SuppressWarnings("deprecation")
					Player target = Main.getPlugin().getServer().getPlayer(argss[0]);
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(argss[0]);
						} catch (Exception e) {
						}
					}
					if (uuid != null) {
						player.sendMessage(ChatColor.RED + "Parece que o nick escolhido ja existe, use outro!");
						return;
					}
					Main.getPlugin().getTagManager().removePlayerTag(player);
					changeNick(player, argss[0]);
					Main.getPlugin().getTagManager().addPlayerTag(player, Tag.NORMAL);
					player.sendMessage(ChatColor.YELLOW + "Voce agora esta disfarcado como '" + argss[0] + "' e sua tag foi setada para NORMAL!");
				}
			}.runTaskAsynchronously(Main.getPlugin());
			return true;
		}
		return false;
	}

	public boolean validate(String username) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = Main.getPlugin().getPermissionManager();
		if (Main.getPlugin().getTorneioManager().isParticipante(p.getUniqueId()))
			return Tag.TORNEIO;
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}

	public void changeNick(final Player p, String nick) {
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		GameProfile profile = player.getProfile();
		String name = "textures";
		String signature = null;
		String valuee = "";
		if (names.containsKey(p.getUniqueId()) && nick.equalsIgnoreCase(names.get(p.getUniqueId()).lastPlayerName)) {
			FakePlayer originalName = names.get(p.getUniqueId());
			profile.getProperties().asMap().remove("textures");
			profile.getProperties().put(name, new Property(name, originalName.value, signature));
			names.remove(p.getUniqueId());
		} else {
			for (Property property : profile.getProperties().asMap().get("textures")) {
				if (property.getName().equals("textures")) {
					valuee = property.getValue();
					break;
				}
			}
			names.put(p.getUniqueId(), new FakePlayer(new String(p.getName()), valuee));
			profile.getProperties().asMap().remove("textures");
			byte[] decode = Base64.getDecoder().decode(valuee);
			Gson gson = new Gson();
			JsonObject jb = gson.fromJson(new String(decode), JsonObject.class);
			String timestamp = jb.get("timestamp").getAsString();
			String json = "{\"timestamp\":" + timestamp + ",\"profileId\":\"" + p.getUniqueId().toString().replace("-", "") + "\",\"profileName\":\"" + nick + "\",\"textures\":{}}";
			byte[] encode = Base64.getEncoder().encode(json.getBytes());
			String value = new String(encode);
			profile.getProperties().put(name, new Property(name, value, signature));
		}
		try {
			Field field = profile.getClass().getDeclaredField("name");
			field.setAccessible(true);
			field.set(profile, nick);
			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(player.getId());
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);
		for (Player pla : Main.getPlugin().getServer().getOnlinePlayers()) {
			EntityPlayer pl = ((CraftPlayer) pla).getHandle();
			pl.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, player));
			if (pla != p) {
				pl.playerConnection.sendPacket(destroy);
				pl.playerConnection.sendPacket(spawn);
			}
			pl.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, player));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entity e : p.getNearbyEntities(8, 8, 8)) {
					if (!(e instanceof Player))
						continue;
					Player pla = (Player) e;
					if (pla.canSee(p)) {
						pla.hidePlayer(p);
						pla.showPlayer(p);
					}
				}
			}
		}.runTask(Main.getPlugin());

	}

	private static class FakePlayer {
		private String lastPlayerName;
		private String value;

		public FakePlayer(String name, String value) {
			lastPlayerName = name;
			this.value = value;
		}
	}
}
