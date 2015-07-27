package me.flame.utils.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.utils.UUIDFetcher;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Fake implements CommandExecutor {
	private Map<UUID, String> names;
	private ServerType type;

	public Fake(ServerType type) {
		this.type = type;
		names = new HashMap<UUID, String>();
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
			if (type == ServerType.RAID) {
				player.sendMessage(ChatColor.RED + "Desculpe mas voce nao pode utilizar esse comando no servidor de Raid");
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
						if (argss[0].equalsIgnoreCase(names.get(player.getUniqueId()))) {
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
		try {
			final Object player = Utils.getHandle(p);
			Method profileMeth = player.getClass().getMethod("getProfile");
			GameProfile profile = (GameProfile) profileMeth.invoke(player);
			for (Player pla : Main.getPlugin().getServer().getOnlinePlayers()) {
				Utils.sendPacket(pla, Utils.newPacketPlayOutPlayerInfo(PlayerInfoAction.REMOVE_PLAYER, player));
			}
			PropertyMap propertyMap = profile.getProperties();
			if (names.containsKey(p.getUniqueId()) && nick.equalsIgnoreCase(names.get(p.getUniqueId()))) {
				names.remove(p.getUniqueId());
			} else {
				if (!names.containsKey(p.getUniqueId())) {
					names.put(p.getUniqueId(), p.getName());
				}
			}
			propertyMap.clear();
			try {
				Field field = profile.getClass().getDeclaredField("name");
				field.setAccessible(true);
				field.set(profile, nick);
				field.setAccessible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			player.getClass().getDeclaredField("displayName").set(player, nick);
			;
			int i = (int) Utils.getMethod(player.getClass(), "getId").invoke(player);
			Object destroy = Utils.newPacketPlayOutEntityDestroy(i);
			Object spawn = Utils.newPacketPlayOutNamedEntitySpawn(player);
			for (Player pla : Main.getPlugin().getServer().getOnlinePlayers()) {
				Utils.sendPacket(pla, Utils.newPacketPlayOutPlayerInfo(PlayerInfoAction.ADD_PLAYER, player));
				if (pla != p) {
					Utils.sendPacket(pla, destroy);
					Utils.sendPacket(pla, spawn);
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Entity e : p.getNearbyEntities(10, 10, 10)) {
						if (!(e instanceof Player))
							continue;
						Player pla = (Player) e;
						if (pla.canSee(p)) {
							pla.hidePlayer(p);
							pla.showPlayer(p);
						}
					}
				}
			}.runTaskLater(Main.getPlugin(), 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
