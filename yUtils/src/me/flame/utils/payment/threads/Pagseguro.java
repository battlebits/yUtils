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

import br.com.uol.pagseguro.domain.AccountCredentials;
import br.com.uol.pagseguro.domain.Transaction;
import br.com.uol.pagseguro.service.TransactionSearchService;

public class Pagseguro extends Thread {
	private BuyManager manager;
	private String transactionCode = "";
	Transaction transaction = null;
	CommandSender sender = null;

	public Pagseguro(BuyManager manager, String transactionCode, CommandSender cmds) {
		this.manager = manager;
		this.transactionCode = transactionCode.toUpperCase();
		sender = cmds;
	}

	public void run() {
		try {
			// TODO Colocar AccountCredentials
			// TODO Colocar Token
			transaction = TransactionSearchService.searchByCode(new AccountCredentials("AccountCredentials", "Token"), transactionCode);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Nao foi possivel encontrar seu pagamento, por favor fale com a staff!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			e.printStackTrace();
			return;
		}
		if (transaction.getStatus().getValue() != 4 && transaction.getStatus().getValue() != 3) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Sua compra foi detectada porem ainda estamos aguardando o pagamento");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			return;
		}
		List<String> itens = new ArrayList<String>();
		for (Object item : transaction.getItems()) {
			String desc = item.toString().split("id: ")[1].split(",")[0];
			if (desc.contains("vz:"))
				itens.add(item.toString());
		}
		for (String item2 : itens) {
			boolean achou = false;
			for (Group group : Group.values())
				if (group.toString().trim().equalsIgnoreCase(item2.split("id: vz:")[1].split(",")[0]))
					achou = true;
			if (!achou) {
				break;
			}
			int days = Integer.parseInt(item2.split("id: vz:")[1].split(",")[1]);
			if (days < 0 || days > 10000) {
				break;
			}
		}

		Calendar now = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		try {
			PreparedStatement addlog = manager.getMySQL().prepareStatement("INSERT INTO `vipzero_pagseguro` (`key`,`nome`,`data`) VALUES ('" + transactionCode + "','" + sender.getName() + "','" + fmt.format(now.getTime()) + "');");
			addlog.execute();
			addlog.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (String item3 : itens)
			for (int i = 0; i < Integer.parseInt(item3.split("quantity: ")[1].split(",")[0]); i++) {
				String grupo = "";
				for (Group group : Group.values())
					if (group.toString().trim().equalsIgnoreCase(item3.split("id: vz:")[1].split(",")[0])) {
						grupo = group.toString().toLowerCase().trim();
						break;
					}
				manager.getServer().dispatchCommand(manager.getServer().getConsoleSender(), "darvip " + sender.getName() + " " + grupo + " " + item3.split("id: vz:")[1].split(",")[1]);
			}
	}

}