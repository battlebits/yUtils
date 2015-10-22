package com.renatojunior.dev.iw3.executors;

import java.util.List;
import java.util.UUID;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.model.ChargebackModel;
import com.renatojunior.dev.iw3.model.OrdersModel;
import com.renatojunior.dev.iw3.model.ProductsModel;

public class ChargebackExecutor {
	private final Application app;
	private final UUID uuid;
	private final OrderExecutor orderexecutor;

	public ChargebackExecutor(Application app, UUID uuid, OrderExecutor orderexecutor) {
		this.app = app;
		this.uuid = uuid;
		this.orderexecutor = orderexecutor;
	}

	public void run() {
		OrdersModel _orders = new OrdersModel(this.app);
		ChargebackModel _chargeback = new ChargebackModel(this.app);
		ProductsModel _products = new ProductsModel(this.app);

		List<String[]> chargebacks = _chargeback.getPlayerChargebacksByUUID(this.uuid);
		for (String[] cb : chargebacks) {
			Integer cb_id = Integer.valueOf(cb[0]);
			Integer cb_orderid = Integer.valueOf(cb[2]);

			int x = 0;
			int y = 0;

			_orders.chargeBackOrder(cb_orderid);

			List<String[]> orders = _orders.getOrderActiveByOrderID(cb_orderid);
			for (String[] order : orders) {
				x++;
				String[] product = _products.getProductByID(Integer.valueOf(order[3]));
				if (product != null) {
					List<String> product_servers = _products.getProductServers(Integer.valueOf(order[3]));
					boolean allow = false;
					for (String server : product_servers) {
						if (this.app.getPlugin().getConfig().getStringList("servers").contains(server)) {
							allow = true;
							break;
						}
					}
					if (allow == true) {
						this.app.getChat().consoleMessage("&c[IW3] Chargeback: &f'" + product[0] + "'&e ..");
						if ((product[5] != null) && (!"".equals(product[5]))) {
							this.orderexecutor.removeProduct(product[5]);
						}
						y++;
						_orders.deleteActiveByID(Integer.valueOf(order[0]));
					}
				}
			}
			if (x == y) {
				_chargeback.deleteChargebackByID(cb_id);
			}
		}
	}
}
