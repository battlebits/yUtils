package com.renatojunior.dev.iw3.controller;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.model.OrdersModel;

public class CommandController implements CommandExecutor {
	private final Application app;
	private final OrdersModel _orders;
	private final String websitestring;

	public CommandController(Application app) {
		this.app = app;
		this._orders = new OrdersModel(app);
		this.websitestring = ("&ahttp://battlebits.com.br");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("iw3")) {
			if (!sender.hasPermission("iw3.commands")) {
				this.app.getChat().sendMessage(sender, "&cVoce nao tem permissao para isto");
				return false;
			}
			if (args.length == 0) {
				this.app.getChat().sendMessage(sender, this.websitestring);
				return false;
			}
			if (("get".equalsIgnoreCase(args[0])) && (args.length == 2)) {
				Player queried = Bukkit.getServer().getPlayerExact(args[1]);
				if (queried == null) {
					this.app.getChat().sendMessage(sender, "&cJogador nao encontrado.");
					return false;
				}
				UUID uuid = queried.getUniqueId();
				if (uuid == null) {
					this.app.getChat().sendMessage(sender, "&cJogador nao encontrado.");
					return false;
				}
				this.app.getChat().sendMessage(sender, "&f---------------------------");
				this.app.getChat().sendMessage(sender, "&a Compras de: " + queried.getName());
				this.app.getChat().sendMessage(sender, "&f---------------------------");

				List<String[]> activelist = this._orders.getActiveByUUID(uuid);

				if (activelist.size() == 0) {
					this.app.getChat().sendMessage(sender, "&c - Nada encontrado");
					this.app.getChat().sendMessage(sender, "&7---------------------------");
					return false;
				}
				for (String[] activeitem : activelist) {
					String _name = activeitem[4];
					String _type = activeitem[6];
					String _days = activeitem[7];
					if ("0".equals(_type)) {
						_days = "Lifetime";
					} else {
						_days = _days + " dia(s)";
					}
					this.app.getChat().sendMessage(sender, "");
					this.app.getChat().sendMessage(sender, "&f Produto: &a" + _name);
					this.app.getChat().sendMessage(sender, "&f Dias restantes: &e" + _days);
					this.app.getChat().sendMessage(sender, "");
				}
				this.app.getChat().sendMessage(sender, "&7---------------------------");

				return true;
			}
			if (("reload".equalsIgnoreCase(args[0])) && (args.length == 1)) {
				this.app.getPlugin().reloadConfig();
				this.app.getChat().sendMessage(sender, "&cPlugin recarregado");
				return true;
			}
		} else if ((cmd.getName().equalsIgnoreCase("compras")) && (args.length == 0)) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;

				UUID uuid = player.getUniqueId();
				if (uuid == null) {
					this.app.getChat().sendMessage(sender, "&cJogador nao encontrado.");
					return false;
				}
				this.app.getChat().sendMessage(sender, "&f---------------------------");
				this.app.getChat().sendMessage(sender, "&b Suas compras");
				this.app.getChat().sendMessage(sender, "&f---------------------------");

				List<String[]> activelist = this._orders.getActiveByUUID(uuid);

				if (activelist.size() == 0) {
					this.app.getChat().sendMessage(sender, "&c - Nada encontrado");
					this.app.getChat().sendMessage(sender, "&7---------------------------");
					return false;
				}
				for (String[] activeitem : activelist) {
					String _name = activeitem[4];
					String _type = activeitem[6];
					String _days = activeitem[7];
					if ("0".equals(_type)) {
						_days = "Lifetime";
					} else {
						_days = _days + " dia(s)";
					}
					this.app.getChat().sendMessage(sender, "");
					this.app.getChat().sendMessage(sender, "&f Produto: &b" + _name);
					this.app.getChat().sendMessage(sender, "&f Dias restantes: &e" + _days);
					this.app.getChat().sendMessage(sender, "");
				}
				this.app.getChat().sendMessage(sender, "&7---------------------------");

				return true;
			}
			this.app.getChat().sendMessage(sender, "&cTente: /iw3 get &7jogador");
			return false;
		}
		this.app.getChat().sendMessage(sender, this.websitestring);
		return false;
	}
}
