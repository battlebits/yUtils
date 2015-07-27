package me.flame.utils.injector;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.io.netty.channel.Channel;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Injector {
	public static void createTinyProtocol(Plugin plugin) {
		new TinyProtocol(plugin) {
			@Override
			public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
				if (channel.attr(NetworkManager.protocolVersion).get() == 47) {
					try {
						if (packet instanceof PacketPlayOutPlayerInfo) {
							PacketPlayOutPlayerInfo packetCopy = (PacketPlayOutPlayerInfo) packet;
							PacketPlayOutPlayerInfo packetplayoutplayerinfo = new PacketPlayOutPlayerInfo();
							Field action = packetplayoutplayerinfo.getClass().getDeclaredField("action");
							action.setAccessible(true);
							action.set(packetplayoutplayerinfo, getField(packetCopy, "action"));
							Field player = packetplayoutplayerinfo.getClass().getDeclaredField("player");
							player.setAccessible(true);
							player.set(packetplayoutplayerinfo, getField(packetCopy, "player"));
							Field gamemode = packetplayoutplayerinfo.getClass().getDeclaredField("gamemode");
							gamemode.setAccessible(true);
							gamemode.set(packetplayoutplayerinfo, getField(packetCopy, "gamemode"));
							Field ping = packetplayoutplayerinfo.getClass().getDeclaredField("ping");
							ping.setAccessible(true);
							ping.set(packetplayoutplayerinfo, getField(packetCopy, "ping"));
							Field username = packetplayoutplayerinfo.getClass().getDeclaredField("username");
							username.setAccessible(true);
							username.set(packetplayoutplayerinfo, null);
							return packetplayoutplayerinfo;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return super.onPacketOutAsync(reciever, channel, packet);
			}
		};
	}

	private static Object getField(Object packet, String fieldName) {
		try {
			Field field = packet.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
