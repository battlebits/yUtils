package me.flame.utils.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.flame.utils.Main;
import me.flame.utils.nms.Utils;
import me.flame.utils.nms.Utils.PlayerInfoAction;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

	@SuppressWarnings("unchecked")
	public void changeNick(final Player p, String nick) {
		try {
			Object player = Utils.getHandle(p);
			Method profileMeth = player.getClass().getMethod("getProfile");
			Object profile = profileMeth.invoke(player);
			String name = "textures";
			String signature = null;
			String valuee = "";
			Object propertyMap = Utils.getMethod(profile.getClass(), "getProperties").invoke(profile);
			if (names.containsKey(p.getUniqueId()) && nick.equalsIgnoreCase(names.get(p.getUniqueId()).lastPlayerName)) {
				FakePlayer originalName = names.get(p.getUniqueId());
				Object asMap = Utils.getMethod(propertyMap.getClass(), "asMap").invoke(propertyMap);
				Utils.getMethod(asMap.getClass(), "remove").invoke(asMap, "textures");
				Utils.getMethod(propertyMap.getClass(), "put").invoke(propertyMap, name, Utils.getPropertyClass().getConstructor(String.class, String.class, String.class).newInstance(name, originalName.value, signature));
				names.remove(p.getUniqueId());
			} else {
				Map<String, Collection<Object>> map = (Map<String, Collection<Object>>) Utils.getMethod(propertyMap.getClass(), "asMap").invoke(propertyMap);
				for (Object property : map.get("textures")) {
					String propertyName = (String) Utils.getMethod(property.getClass(), "getName").invoke(property);
					if (propertyName.equals("textures")) {
						valuee = (String) Utils.getMethod(property.getClass(), "getValue").invoke(property);
						break;
					}
				}
				names.put(p.getUniqueId(), new FakePlayer(new String(p.getName()), valuee));
				Object asMap = Utils.getMethod(propertyMap.getClass(), "asMap").invoke(propertyMap);
				Utils.getMethod(asMap.getClass(), "remove").invoke(asMap, "textures");
				byte[] decode = Base64.getDecoder().decode(valuee);
				Object gson = Utils.getGsonClass().newInstance();
				Object jb = Utils.getMethod(gson.getClass(), "fromJson").invoke(gson, new String(decode), Utils.getJsonObjectClass());
				Object times = Utils.getMethod(jb.getClass(), "get").invoke(jb, "timestamp");
				String timestamp = (String) Utils.getMethod(times.getClass(), "getAsString").invoke(times);
				String json = "{\"timestamp\":" + timestamp + ",\"profileId\":\"" + p.getUniqueId().toString().replace("-", "") + "\",\"profileName\":\"" + nick + "\",\"textures\":{}}";
				byte[] encode = Base64.getEncoder().encode(json.getBytes());
				String value = new String(encode);
				Utils.getMethod(propertyMap.getClass(), "put").invoke(propertyMap, name, Utils.getPropertyClass().getConstructor(String.class, String.class, String.class).newInstance(name, value, signature));
			}
			try {
				Field field = profile.getClass().getDeclaredField("name");
				field.setAccessible(true);
				field.set(profile, nick);
				field.setAccessible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object destroy = Utils.newPacketPlayOutEntityDestroy((int) Utils.getMethod(player.getClass(), "getId").invoke(player));
			Object spawn = Utils.newPacketPlayOutNamedEntitySpawn(player);
			for (Player pla : Main.getPlugin().getServer().getOnlinePlayers()) {
				Utils.sendPacket(pla, Utils.newPacketPlayOutPlayerInfo(PlayerInfoAction.REMOVE_PLAYER, player));
				if (pla != p) {
					Utils.sendPacket(pla, destroy);
					Utils.sendPacket(pla, spawn);
				}
				Utils.sendPacket(pla, Utils.newPacketPlayOutPlayerInfo(PlayerInfoAction.ADD_PLAYER, player));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getMethodObj(Method method) {
		return null;
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
