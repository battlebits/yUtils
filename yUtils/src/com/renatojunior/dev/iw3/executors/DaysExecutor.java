package com.renatojunior.dev.iw3.executors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.model.OrdersModel;
import com.renatojunior.dev.iw3.model.ProductsModel;

public class DaysExecutor {
	private final Application app;
	private final UUID uuid;
	private final OrderExecutor orderexecutor;

	public DaysExecutor(Application app, UUID uuid, OrderExecutor orderexecutor) {
		this.app = app;
		this.uuid = uuid;
		this.orderexecutor = orderexecutor;
	}

	public static int getDifferenceDays(String d1, String d2) throws ParseException {
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = myFormat.parse(d1);
		Date date2 = myFormat.parse(d2);
		long diff = date2.getTime() - date1.getTime();
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public void run() {
		OrdersModel _orders = new OrdersModel(this.app);
		ProductsModel _products = new ProductsModel(this.app);

		String today = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

		List<String[]> query = _orders.getActiveByUUID(this.uuid);
		for (String[] item : query) {
			if (Integer.valueOf(item[6]).intValue() != 0) {
				Integer _id = Integer.valueOf(item[0]);
				Integer _itemid = Integer.valueOf(item[3]);
				Integer _days = Integer.valueOf(item[7]);
				Integer _diff = Integer.valueOf(0);
				try {
					_diff = Integer.valueOf(getDifferenceDays(item[2], today));
				} catch (Exception err) {
					this.app.getChat().consoleMessage("&4[IW3][Error] &Execution error: run (DaysExecutor.java)");
					this.app.getChat().consoleMessage(err.toString());
				}
				if (_diff.intValue() != 0) {
					if (_days.intValue() - _diff.intValue() < 0) {
						String[] product = _products.getProductByID(_itemid);
						if (product != null) {
							List<String> product_servers = _products.getProductServers(_itemid);
							boolean allow = false;
							for (String server : product_servers) {
								if (this.app.getPlugin().getConfig().getStringList("servers").contains(server)) {
									allow = true;
									break;
								}
							}
							if (allow == true) {
								this.app.getChat().consoleMessage("&c[IW3] Expirado: &f'" + product[0] + "'&c ..");
								this.orderexecutor.removeActive(_orders, _itemid, null, product[1], product[4]);
							}
						}
					} else if (_orders.updateActiveDays(_id, Integer.valueOf(_days.intValue() - _diff.intValue())) == 1) {
						_orders.updateActiveLastUpdate(_id, today);
					}
				}
			}
		}
	}
}
