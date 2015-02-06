package me.flame.utils.payment.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.flame.utils.payment.BuyManager;
import me.flame.utils.permissions.enums.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import paypalnvp.profile.BaseProfile;
import paypalnvp.profile.Profile;
import paypalnvp.request.GetTransactionDetails;

public class Paypal extends Thread {

	private BuyManager manager;
	private String transactID = "";
	private CommandSender sender = null;

	public Paypal(BuyManager manager, String transactID2, CommandSender cmdss) {
		this.manager = manager;
		transactID = transactID2.toUpperCase();
		sender = cmdss;

	}

	@Override
	public void run() {
		paypalnvp.core.PayPal pp = null;
		GetTransactionDetails tr = null;
		try {
			Profile user = new BaseProfile.Builder("usuario", "senha").signature("assinatura").build();
			pp = new paypalnvp.core.PayPal(user, paypalnvp.core.PayPal.Environment.LIVE);
			tr = new GetTransactionDetails(transactID);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Nao foi possivel encontrar seu pagamento, por favor fale com a staff!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			e.printStackTrace();
			return;
		}
		if (pp == null || tr == null) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Nao foi possivel encontrar seu pagamento, por favor fale com a staff!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			return;
		}

		pp.setResponse(tr);

		if (!tr.getNVPResponse().containsKey("PAYMENTSTATUS")) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Nao foi possivel encontrar seu pagamento, por favor fale com a staff!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			return;
		}
		if (!tr.getNVPResponse().get("PAYMENTSTATUS").equals("Completed")) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Sua compra foi detectada porem ainda estamos aguardando o pagamento");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			return;
		}

		List<String> itens = new ArrayList<String>();
		for (String key : tr.getNVPResponse().keySet())
			if (key.startsWith("L_NAME")) {
				if (tr.getNVPResponse().get(key).contains("(vz:"))
					for (int i = 0; i < Integer.parseInt(tr.getNVPResponse().get("L_QTY" + key.charAt(key.length() - 1))); i++)
						itens.add(tr.getNVPResponse().get(key).split("\\(vz:")[1].split("\\)")[0] + "," + tr.getNVPResponse().get("L_QTY" + key.charAt(key.length() - 1)));
			}
		if (itens.size() == 0 || itens == null) {
			sender.sendMessage(ChatColor.RED + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.RED + "Um erro ocorreu enquanto tentava validar o pedido");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.RED + "-------------------------------------------------------------------");
			return;
		}

		for (String item2 : itens) {
			boolean achou = false;
			for (Group group : Group.values())
				if (group.toString().trim().equalsIgnoreCase(item2.split(",")[0]))
					achou = true;
			if (!achou) {
				break;
			}
			int days = Integer.parseInt(item2.split(",")[1]);
			if (days < 0 || days > 10000) {
				break;
			}
		}

		Calendar now = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		try {
			PreparedStatement addlog = manager.getMySQL().prepareStatement("INSERT INTO `vipzero_paypal` (`key`,`nome`,`data`) VALUES ('" + transactID + "','" + sender.getName() + "','" + fmt.format(now.getTime()) + "');");
			addlog.execute();
			addlog.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (String item3 : itens)
			for (int i = 0; i < Integer.parseInt(item3.split(",")[2]); i++) {
				String grupo = "";
				for (Group group : Group.values())
					if (group.toString().trim().equalsIgnoreCase(item3.split(",")[0])) {
						grupo = group.toString().trim();
						break;
					}
				manager.getServer().dispatchCommand(manager.getServer().getConsoleSender(), "darvip" + " " + sender.getName() + " " + grupo + " " + item3.split(",")[1]);
			}
	}
}
