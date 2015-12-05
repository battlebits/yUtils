package me.flame.utils.injector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.flame.utils.Main;
import me.flame.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.util.io.netty.channel.Channel;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.inventivegames.holograms.HologramAPI;
import de.inventivegames.holograms.HologramOffsets;
import de.inventivegames.holograms.reflection.AccessUtil;
import de.inventivegames.holograms.reflection.NMSClass;
import de.inventivegames.holograms.reflection.NMUClass;

public class Injector {
	@SuppressWarnings("unused")
	public static void createTinyProtocol(Plugin plugin) {
		new TinyProtocol(plugin) {
			@SuppressWarnings("boxing")
			@Override
			public Object onPacketOutAsync(final Player reciever, Channel channel, Object packet) {
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
				if (channel.attr(NetworkManager.protocolVersion).get() != 47)
					return super.onPacketOutAsync(reciever, channel, packet);
				try {
					if (packet instanceof PacketPlayOutSpawnEntityLiving) {
						PacketPlayOutSpawnEntityLiving living = (PacketPlayOutSpawnEntityLiving) packet;

						int id = (int) ReflectionUtils.getPrivateFieldObject(living, "a");

						if (!HologramAPI.isHologramEntity(id)) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						Entity entity = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id).getBukkitEntity();

						if (entity == null) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						if (entity.getType() == EntityType.HORSE) {

							ReflectionUtils.setPrivateFieldObject(living, "b", 30); // Armor
																					// stands
																					// as
																					// living
																					// entities
																					// ID

							DataWatcher watcher = (DataWatcher) ReflectionUtils.getPrivateFieldObject(living, "l");

							if (watcher != null) {
								fixIndexes(watcher);
							}

							return living;
						}
					} else if (packet instanceof PacketPlayOutSpawnEntity) {

						PacketPlayOutSpawnEntity spawn = (PacketPlayOutSpawnEntity) packet;

						final int id = (int) ReflectionUtils.getPrivateFieldObject(spawn, "a");

						if (!HologramAPI.isHologramEntity(id)) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						Entity entity = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id).getBukkitEntity();

						if (entity == null) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						if (entity.getType() == EntityType.WITHER_SKULL) {

							ReflectionUtils.setPrivateFieldObject(spawn, "j", 78); // The
																					// object
																					// ID
																					// for
																					// armor
																					// stands

							final DataWatcher watcher = new DataWatcher(((CraftWorld) reciever.getWorld()).getHandle().getEntity(id));

							fixIndexes(watcher);

							new BukkitRunnable() {
								@Override
								public void run() {
									PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(id, watcher, true);
									((CraftPlayer) reciever).getHandle().playerConnection.sendPacket(metadata);
								}
							}.runTaskLater(Main.getPlugin(), 1);

							return spawn;
						}
					} else if (packet instanceof PacketPlayOutEntityMetadata) {

						PacketPlayOutEntityMetadata metadata = (PacketPlayOutEntityMetadata) packet;

						int id = (int) ReflectionUtils.getPrivateFieldObject(metadata, "a");
						if (!HologramAPI.isHologramEntity(id))
							return super.onPacketOutAsync(reciever, channel, packet);

						CraftEntity e = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id).getBukkitEntity();

						if (e == null) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						if (e.getType() != EntityType.HORSE)
							return super.onPacketOutAsync(reciever, channel, packet);
						DataWatcher watcher = e.getHandle().getDataWatcher();

						if (watcher != null) {
							fixIndexes(watcher);
							ReflectionUtils.setPrivateFieldObject(metadata, "b", watcher.c());

							return metadata;
						}
					} else if (packet instanceof PacketPlayOutAttachEntity) {

						PacketPlayOutAttachEntity attach = (PacketPlayOutAttachEntity) packet;

						int id1 = (int) ReflectionUtils.getPrivateFieldObject(attach, "b");
						int id2 = (int) ReflectionUtils.getPrivateFieldObject(attach, "c");

						if (id2 == -1 || !HologramAPI.isHologramEntity(id2)) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						Entity passenger = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id1).getBukkitEntity();
						Entity vehicle = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id2).getBukkitEntity();

						if (vehicle != null && passenger != null) {
							final PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport();
							Location loc = vehicle.getLocation();
							AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("a")).set(teleport, id2);
							AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("b")).set(teleport, (int) (loc.getX() * 32D));
							AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("d")).set(teleport, (int) (loc.getZ() * 32D));
							AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("e")).set(teleport, (byte) (int) (loc.getYaw() * 256F / 360F));
							AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("f")).set(teleport, (byte) (int) (loc.getPitch() * 256F / 360F));
							if (passenger.getType() == EntityType.HORSE) {
								AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("c")).set(teleport, (int) ((loc.getY() - HologramOffsets.WITHER_SKULL_HORSE + HologramOffsets.ARMOR_STAND_PACKET) * 32D));
							} else if (passenger.getType() == EntityType.DROPPED_ITEM || passenger.getType() == EntityType.SLIME) {
								AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("c")).set(teleport, (int) ((loc.getY() + HologramOffsets.ARMOR_STAND_DEFAULT) * 32D));
							}

							new BukkitRunnable() {
								@Override
								public void run() {
									((CraftPlayer) reciever).getHandle().playerConnection.sendPacket(teleport);
								}
							}.runTaskLater(Main.getPlugin(), 1);
							return null;
						}
					} else if (packet instanceof PacketPlayOutEntityTeleport) {

						PacketPlayOutEntityTeleport teleport = (PacketPlayOutEntityTeleport) packet;

						int id = (int) ReflectionUtils.getPrivateFieldObject(teleport, "a");
						if (!HologramAPI.isHologramEntity(id)) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						Entity entity = ((CraftWorld) reciever.getWorld()).getHandle().getEntity(id).getBukkitEntity();

						if (entity == null) {
							return super.onPacketOutAsync(reciever, channel, packet);
						}

						if (entity.getType() == EntityType.WITHER_SKULL) {

							Entity passenger = entity.getPassenger();

							if (passenger == null) {
								return super.onPacketOutAsync(reciever, channel, packet);
							}

							if (passenger.getType() == EntityType.DROPPED_ITEM || passenger.getType() == EntityType.SLIME) {
								AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("c")).set(teleport, (int) ((entity.getLocation().getY() - HologramOffsets.ARMOR_STAND_DEFAULT) * 32D));
							} else if (passenger.getType() == EntityType.HORSE) {
								AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("a")).set(teleport, entity.getPassenger().getEntityId());
								AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("c")).set(teleport, (int) ((entity.getLocation().getY() - HologramOffsets.WITHER_SKULL_HORSE + HologramOffsets.ARMOR_STAND_PACKET) * 32D));
							}
							return teleport;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return super.onPacketOutAsync(reciever, channel, packet);
			}
		};
	}

	@SuppressWarnings("boxing")
	private static void fixIndexes(DataWatcher watcher) throws Exception {
		Object map_1_8 = AccessUtil.setAccessible(NMSClass.DataWatcher.getDeclaredField("dataValues")).get(watcher);
		NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("put", int.class, Object.class).invoke(map_1_8, 10, NMSClass.WatchableObject.getConstructor(int.class, int.class, Object.class).newInstance(0, 10, (byte) 1));
		List<Integer> toRemove = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Object current = NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("get", int.class).invoke(map_1_8, i);
			if (current == null) {
				continue;
			}
			int index = AccessUtil.setAccessible(NMSClass.WatchableObject.getDeclaredField("b")).getInt(current);
			if (index == 2) {

			} else if (index != 3) {
				toRemove.add(Integer.valueOf(index));
			}
		}
		for (Integer i : toRemove) {
			NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("remove", int.class).invoke(map_1_8, i);
		}
		NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("put", int.class, Object.class).invoke(map_1_8, 0, NMSClass.WatchableObject.getConstructor(int.class, int.class, Object.class).newInstance(0, 0, (byte) 32));
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
