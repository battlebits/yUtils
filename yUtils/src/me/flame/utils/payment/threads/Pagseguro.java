package me.flame.utils.payment.threads;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.flame.utils.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import br.com.uol.pagseguro.domain.AccountCredentials;
import br.com.uol.pagseguro.domain.Transaction;
import br.com.uol.pagseguro.service.TransactionSearchService;

public class Pagseguro extends Thread {
	private Main plugin;
	private String transactionCode = "";
	Transaction transaction = null;
	CommandSender sender = null;

	public Pagseguro(Main plugin, String transactionCode, CommandSender cmds) {
		this.plugin = plugin;
		this.transactionCode = transactionCode.toUpperCase();
		sender = cmds;
	}

	public void run() {
		try {
			transaction = TransactionSearchService.searchByCode(new AccountCredentials(plugin.getConfig().getString("pagseguro.email"), plugin.getConfig().getString("pagseguro.token")), transactionCode);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Nao foi possivel encontrar seu pagamento, por favor fale com a staff!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
			e.printStackTrace();
			return;
		}
		// printTransaction(transaction);
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
			for (String gName : plugin.getConfig().getStringList("vip_groups"))
				if (gName.trim().equalsIgnoreCase(item2.split("id: vz:")[1].split(",")[0]))
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
			Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
			PreparedStatement addlog = con.prepareStatement("INSERT INTO `vipzero_pagseguro` (`key`,`nome`,`data`) VALUES ('" + transactionCode + "','" + sender.getName() + "','" + fmt.format(now.getTime()) + "');");
			addlog.execute();
			addlog.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (String item3 : itens)
			for (int i = 0; i < Integer.parseInt(item3.split("quantity: ")[1].split(",")[0]); i++) {
				String grupo = "";
				for (String gName : plugin.getConfig().getStringList("vip_groups"))
					if (gName.trim().equalsIgnoreCase(item3.split("id: vz:")[1].split(",")[0])) {
						grupo = gName.trim();
						break;
					}
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "darvip " + sender.getName() + " " + grupo + " " + item3.split("id: vz:")[1].split(",")[1]);
			}
	}

	/*
	 * private static void printTransaction(Transaction transaction) {
	 * System.out.println("code: " + transaction.getCode());
	 * System.out.println("date: " + transaction.getDate());
	 * System.out.println("discountAmount: " + transaction.getDiscountAmount());
	 * System.out.println("extraAmount: " + transaction.getExtraAmount());
	 * System.out.println("feeAmount: " + transaction.getFeeAmount());
	 * System.out.println("grossAmount: " + transaction.getGrossAmount());
	 * System.out.println("installmentCount: " +
	 * transaction.getInstallmentCount()); System.out.println("itemCount: " +
	 * transaction.getItemCount()); for (int i = 0; i <
	 * transaction.getItems().size(); i++) { System.out.println("item[" + (i +
	 * 1) + "]: " + transaction.getItems().get(i)); }
	 * System.out.println("lastEventDate: " + transaction.getLastEventDate());
	 * System.out.println("netAmount: " + transaction.getNetAmount());
	 * System.out.println("paymentMethodType: " +
	 * transaction.getPaymentMethod().getCode().getValue());
	 * System.out.println("paymentMethodcode: " +
	 * transaction.getPaymentMethod().getType().getValue());
	 * System.out.println("reference: " + transaction.getReference());
	 * System.out.println("senderEmail: " + transaction.getSender().getEmail());
	 * if (transaction.getSender() != null) { System.out.println("senderPhone: "
	 * + transaction.getSender().getPhone()); } if (transaction.getShipping() !=
	 * null) { System.out.println("shippingType: " +
	 * transaction.getShipping().getType().getValue());
	 * System.out.println("shippingCost: " +
	 * transaction.getShipping().getCost()); if
	 * (transaction.getShipping().getAddress() != null) {
	 * System.out.println("shippingAddressCountry: " +
	 * transaction.getShipping().getAddress().getCountry());
	 * System.out.println("shippingAddressState: " +
	 * transaction.getShipping().getAddress().getState());
	 * System.out.println("shippingAddressCity: " +
	 * transaction.getShipping().getAddress().getCity());
	 * System.out.println("shippingAddressPostalCode: " +
	 * transaction.getShipping().getAddress().getPostalCode());
	 * System.out.println("shippingAddressDistrict: " +
	 * transaction.getShipping().getAddress().getDistrict());
	 * System.out.println("shippingAddressStreet: " +
	 * transaction.getShipping().getAddress().getStreet());
	 * System.out.println("shippingAddressNumber: " +
	 * transaction.getShipping().getAddress().getNumber());
	 * System.out.println("shippingAddressComplement: " +
	 * transaction.getShipping().getAddress().getComplement()); } }
	 * System.out.println("status: " + transaction.getStatus().getValue());
	 * System.out.println("type: " + transaction.getType().getValue()); }
	 */
}