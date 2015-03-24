package me.flame.utils.commands;

import java.lang.reflect.Field;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.utils.UUIDFetcher;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;

public class Fake implements CommandExecutor {

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
			new BukkitRunnable() {
				@Override
				public void run() {
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
					changeNick(player, argss[0]);
					Main.getPlugin().getTagManager().addPlayerTag(player, Tag.NORMAL);
					player.sendMessage(ChatColor.YELLOW + "Voce agora esta disfarcado como '" + argss[0] + "'!");
				}
			}.runTaskAsynchronously(Main.getPlugin());
			return true;
		}
		return false;
	}

	public void changeNick(Player p, String nick) {
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		GameProfile profile = player.getProfile();
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
			pl.playerConnection.sendPacket(destroy);
			pl.playerConnection.sendPacket(spawn);
		}
	}
}
