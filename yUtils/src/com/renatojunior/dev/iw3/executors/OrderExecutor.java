package com.renatojunior.dev.iw3.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.model.OrdersModel;
import com.renatojunior.dev.iw3.model.ProductsModel;

public class OrderExecutor {
	private final Application app;
	private final UUID uuid;
	private final String playername;
	private String lastproduct;
	private String lastproductdays;
	private String lastproductquant;

	public OrderExecutor(Application app, UUID uuid, String playername) {
		this.app = app;
		this.uuid = uuid;
		this.playername = playername;
	}

	public boolean run() {
		OrdersModel _orders = new OrdersModel(this.app);
		ProductsModel _products = new ProductsModel(this.app);
		List<String[]> query = _orders.getPlayerOrdersByUUID(this.uuid);
		this.app.getChat().consoleMessage("&e[IW3] Analisando &f" + this.playername + "&e / &f" + this.uuid.toString());
		List<String> back_products = new ArrayList<>();
		if ((query == null) || (query.toArray().length == 0)) {
			return false;
		}
		this.app.getChat().consoleMessage("&e[IW3] Possui itens para dar para: &f" + this.playername + "&e / &f" + this.uuid.toString());
		for (String[] item : query) {
			Integer item_id = Integer.valueOf(item[0]);

			String item_product_list = String.valueOf(item[3]);

			String[] products_raw = item_product_list.split("%");
			for (String product_imploded : products_raw) {
				String[] products_exploded = product_imploded.split("-");
				Integer _id = Integer.valueOf(products_exploded[0].replace(".", ""));
				Integer _quant = Integer.valueOf(products_exploded[1]);

				String[] product = _products.getProductByID(_id);

				List<String> product_servers = _products.getProductServers(_id);
				boolean allow = false;
				for (String server : product_servers) {
					if (this.app.getPlugin().getConfig().getStringList("servers").contains(server)) {
						allow = true;
						break;
					}
				}
				if ((product == null) || (!allow)) {
					String cupom = "";
					if (products_exploded.length == 4) {
						cupom = products_exploded[3];
					}
					back_products.add("." + _id + ".-" + _quant + "-" + products_exploded[2] + "-" + cupom);
				} else if (giveActive(_orders, _id, product[0], product[1], Integer.valueOf(product[2]), _quant, item_id) == 1) {
					this.app.getChat().consoleMessage("&b[IW3] Adicionando: &f'" + product[0] + "'&b (x" + _quant + ") ..");

					this.lastproduct = product[0];
					this.lastproductdays = String.valueOf(Integer.valueOf(product[2]).intValue() * _quant.intValue());
					this.lastproductquant = String.valueOf(_quant);
					String[] message = { this.playername, "" };
					if (Integer.valueOf(product[2]).intValue() == 0) {
						message[1] = prepareCommand("&a[!]&f Voce recebeu: &f[PRODUCT-NAME]");
					} else {
						message[1] = prepareCommand("&a[!]&f Voce recebeu: &7[PRODUCT-NAME] &c(x[PRODUCT-QUANT]) &a(Dias adicionados: [PRODUCT-DAYS])");
					}
					this.app.addChatScheduler(message);
					if ((product[3] != null) && (!"".equals(product[3]))) {
						giveProduct(product[3], _quant);
					}
				}
			}
			String update = "dispatched";
			if (back_products.toArray().length > 0) {
				update = StringUtils.join(back_products, "%");
			}
			_orders.updateProductListByID(item_id, update);
		}
		return false;
	}

	public void giveProduct(String onstart, Integer quant) {
		for (int x = 0; x < quant.intValue(); x++) {
			String[] _onstart = String.valueOf(onstart).split(";");
			for (String cmd : _onstart) {
				this.app.getChat().consoleMessage("&8[IW3][Start] Executing: '&7" + prepareCommand(cmd) + "&8'  ...");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prepareCommand(cmd));
			}
		}
	}

	public void removeProduct(String onstop) {
		String[] _onstop = String.valueOf(onstop).split(";");
		for (String cmd : _onstop) {
			this.app.getChat().consoleMessage("&8[IW3][Remove] Executing: '&7" + prepareCommand(cmd) + "&8'  ...");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prepareCommand(cmd));
		}
	}

	public int giveActive(OrdersModel om, Integer itemid, String itemname, String groupname, Integer days, Integer quant, Integer order) {
		String[] active = om.getOrderActiveByItemID(this.uuid, itemid);
		if (active == null) {
			return om.createActive(this.uuid, itemid, itemname, groupname, days, quant, order);
		}
		if (Integer.valueOf(active[6]).intValue() == 0) {
			return 1;
		}
		return om.updateActiveDays(Integer.valueOf(active[0]), Integer.valueOf(Integer.valueOf(active[7]).intValue() + days.intValue() * quant.intValue()));
	}

	public int removeActive(OrdersModel om, Integer itemid, Integer days, String groupname, String onstop) {
		String[] active = om.getOrderActiveByItemID(this.uuid, itemid);
		Integer grouplength = om.getGroupLength(groupname);
		if (active == null) {
			return 0;
		}
		if (days == null) {
			if (grouplength.intValue() <= 1) {
				removeProduct(onstop);
			}
			return om.deleteActiveByID(Integer.valueOf(active[0]));
		}
		if (Integer.valueOf(active[6]).intValue() == 1) {
			Integer newdays = Integer.valueOf(Integer.valueOf(active[6]).intValue() - days.intValue());
			if (newdays.intValue() < 0) {
				if (grouplength.intValue() <= 1) {
					removeProduct(onstop);
				}
				return om.deleteActiveByID(Integer.valueOf(active[0]));
			}
			return om.updateActiveDays(Integer.valueOf(active[0]), newdays);
		}
		return 1;
	}

	public String prepareCommand(String cmd) {
		cmd = cmd.replace("[UUID]", this.uuid.toString());

		cmd = cmd.replace("[PLAYER]", this.playername);
		if ((this.lastproduct != null) && (!"".equals(this.lastproduct))) {
			cmd = cmd.replace("[PRODUCT-NAME]", this.lastproduct);
		}
		if ((this.lastproductdays != null) && (!"".equals(this.lastproductdays))) {
			cmd = cmd.replace("[PRODUCT-DAYS]", this.lastproductdays);
		}
		if ((this.lastproductquant != null) && (!"".equals(this.lastproductquant))) {
			cmd = cmd.replace("[PRODUCT-QUANT]", this.lastproductquant);
		}
		return cmd;
	}
}
