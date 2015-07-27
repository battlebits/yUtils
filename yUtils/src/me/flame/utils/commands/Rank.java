package me.flame.utils.commands;

import me.flame.utils.Main;
import me.flame.utils.ranking.constructors.Account;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Rank implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rank")) {
			sender.sendMessage(ChatColor.AQUA + "A rede Battlebits possui um sistema de ranking conforme a quantidade de XP você possui");
			for (int i = me.flame.utils.ranking.enums.Rank.values().length; i > 0; i--) {
				me.flame.utils.ranking.enums.Rank rank = me.flame.utils.ranking.enums.Rank.values()[i - 1];
				sender.sendMessage(rank.getSymbol() + " " + rank.toString());
			}
			if (sender instanceof Player) {
				Account account = Main.getPlugin().getRankingManager().getAccount((Player) sender);
				sender.sendMessage(ChatColor.GREEN + "Sua liga atual: " + account.getLiga().getSymbol() + " " + account.getLiga().toString());
				sender.sendMessage(ChatColor.GREEN + "Seu XP atual: " + account.getXp());
				if (account.getLiga().ordinal() + 1 < me.flame.utils.ranking.enums.Rank.values().length) {
					me.flame.utils.ranking.enums.Rank rank = me.flame.utils.ranking.enums.Rank.values()[account.getLiga().ordinal() + 1];
					sender.sendMessage(ChatColor.GREEN + "Proxima liga: " + rank.getSymbol() + " " + rank.toString());
					sender.sendMessage(ChatColor.GREEN + "XP necessario para proxima liga: " + (account.getLiga().getMax() - account.getXp() + 1));
				} else {
					sender.sendMessage(ChatColor.GOLD + "Voce está na maior liga! Parabens!");
				}
			}
		}
		return false;
	}

}
