package com.renatojunior.dev.iw3.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.classes.MysqlAPI;
import com.renatojunior.dev.iw3.constructor.ResultMap;

public class OrdersModel {
	private final Application app;

	public OrdersModel(Application app) {
		this.app = app;
	}

	public List<String[]> getPlayerOrdersByUUID(UUID player) {
		List<String[]> query = new ArrayList<>();
		String uuid = MysqlAPI.string(player.toString().replace("-", ""));
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras.id, ws_compras.uuid, ws_compras.giftuuid, ws_compras.product_list, ws_compras.time, ws_compras.status, ws_compras.dispatch FROM ws_compras WHERE ((ws_compras.uuid = '" + uuid + "' AND ws_compras.giftuuid = '') OR (ws_compras.giftuuid = '" + uuid + "')) " + "AND ws_compras.dispatch = 1 " + "AND ws_compras.product_list != 'dispatched' " + "AND ws_compras.product_list != 'chargeback' " + "AND ws_compras.active = 1 " + "ORDER BY " + "ws_compras.id DESC");
			while (_query.next()) {
				String[] result = { "", "", "", "", "", "", "" };
				result[0] = _query.getString("id");
				result[1] = _query.getString("uuid");
				result[2] = _query.getString("giftuuid");
				result[3] = _query.getString("product_list");
				result[4] = _query.getString("time");
				result[5] = _query.getString("status");
				result[6] = _query.getString("dispatch");
				query.add(result);
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getPlayerOrdersByUUID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public String[] getOrderByID(Integer id) {
		String[] query = null;
		String _id = MysqlAPI.string(id.toString());
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras.id, ws_compras.uuid, ws_compras.giftuuid, ws_compras.product_list, ws_compras.time, ws_compras.status, ws_compras.dispatch FROM ws_compras WHERE ws_compras.id = " + _id + " " + "AND ws_compras.active = 1 " + "LIMIT 1");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "", "" };
				pre_query[0] = _query.getString("id");
				pre_query[1] = _query.getString("uuid");
				pre_query[2] = _query.getString("giftuuid");
				pre_query[3] = _query.getString("product_list");
				pre_query[4] = _query.getString("time");
				pre_query[5] = _query.getString("status");
				pre_query[6] = _query.getString("dispatch");
				query = pre_query;
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getOrderByID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public Integer updateProductListByID(Integer id, String products) {
		String _id = MysqlAPI.string(String.valueOf(id));
		String _products = MysqlAPI.string(products);

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("UPDATE ws_compras SET ws_compras.product_list = '" + _products + "' " + "WHERE " + "ws_compras.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: updateProductListByID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query;
	}

	public Integer chargeBackOrder(Integer orderid) {
		String _id = MysqlAPI.string(String.valueOf(orderid));

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("UPDATE ws_compras SET ws_compras.product_list = 'chargeback' WHERE ws_compras.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: resetOrder (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query;
	}

	public String[] getOrderActiveByItemID(UUID uuid, Integer itemid) {
		String[] query = null;
		String _uuid = MysqlAPI.string(uuid.toString().replace("-", ""));
		String _itemid = MysqlAPI.string(String.valueOf(itemid));
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras_active.id, ws_compras_active.order, ws_compras_active.lastupdate, ws_compras_active.item_id, ws_compras_active.item_name, ws_compras_active.item_groupname, ws_compras_active.type, ws_compras_active.days FROM ws_compras_active WHERE ws_compras_active.uuid = '" + _uuid + "' " + "AND ws_compras_active.item_id = " + _itemid + " " + "ORDER BY " + "ws_compras_active.id DESC " + "LIMIT 1");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "", "", "" };
				pre_query[0] = _query.getString("id");
				pre_query[1] = _query.getString("order");
				pre_query[2] = _query.getString("lastupdate");
				pre_query[3] = _query.getString("item_id");
				pre_query[4] = _query.getString("item_name");
				pre_query[5] = _query.getString("item_groupname");
				pre_query[6] = _query.getString("type");
				pre_query[7] = _query.getString("days");
				query = pre_query;
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getOrderActiveByItemID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public List<String[]> getOrderActiveByOrderID(Integer orderid) {
		List<String[]> query = new ArrayList<>();
		String _orderid = MysqlAPI.string(String.valueOf(orderid));
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras_active.id, ws_compras_active.order, ws_compras_active.lastupdate, ws_compras_active.item_id, ws_compras_active.item_name, ws_compras_active.item_groupname, ws_compras_active.type, ws_compras_active.days FROM ws_compras_active WHERE ws_compras_active.order = " + _orderid + " " + "ORDER BY " + "ws_compras_active.id ASC");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "", "", "" };
				pre_query[0] = _query.getString("id");
				pre_query[1] = _query.getString("order");
				pre_query[2] = _query.getString("lastupdate");
				pre_query[3] = _query.getString("item_id");
				pre_query[4] = _query.getString("item_name");
				pre_query[5] = _query.getString("item_groupname");
				pre_query[6] = _query.getString("type");
				pre_query[7] = _query.getString("days");
				query.add(pre_query);
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getOrderActiveByOrderID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public String[] getOrderActiveByItemGroupName(UUID uuid, String groupname) {
		String[] query = null;
		String _uuid = MysqlAPI.string(uuid.toString().replace("-", ""));
		String _groupname = MysqlAPI.string(String.valueOf(groupname));
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras_active.id, ws_compras_active.order, ws_compras_active.lastupdate, ws_compras_active.item_id, ws_compras_active.item_name, ws_compras_active.item_groupname, ws_compras_active.type, ws_compras_active.days FROM ws_compras_active WHERE ws_compras_active.item_groupname = '" + _groupname + "' " + "AND ws_compras_active.uuid = '" + _uuid + "' " + "ORDER BY " + "ws_compras_active.id DESC " + "LIMIT 1");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "", "", "" };
				pre_query[0] = _query.getString("id");
				pre_query[1] = _query.getString("order");
				pre_query[2] = _query.getString("lastupdate");
				pre_query[3] = _query.getString("item_id");
				pre_query[4] = _query.getString("item_name");
				pre_query[5] = _query.getString("item_groupname");
				pre_query[6] = _query.getString("type");
				pre_query[7] = _query.getString("days");
				query = pre_query;
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getOrderActiveByItemName (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public List<String[]> getActiveByUUID(UUID player) {
		List<String[]> query = new ArrayList<>();
		String uuid = MysqlAPI.string(player.toString().replace("-", ""));
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras_active.id, ws_compras_active.order, ws_compras_active.lastupdate, ws_compras_active.item_id, ws_compras_active.item_name, ws_compras_active.item_groupname, ws_compras_active.type, ws_compras_active.days FROM ws_compras_active WHERE ws_compras_active.uuid = '" + uuid + "' " + "ORDER BY " + "ws_compras_active.id DESC");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "", "", "" };
				pre_query[0] = _query.getString("id");
				pre_query[1] = _query.getString("order");
				pre_query[2] = _query.getString("lastupdate");
				pre_query[3] = _query.getString("item_id");
				pre_query[4] = _query.getString("item_name");
				pre_query[5] = _query.getString("item_groupname");
				pre_query[6] = _query.getString("type");
				pre_query[7] = _query.getString("days");
				query.add(pre_query);
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getActiveByUUID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public int updateActiveDays(Integer activeid, Integer newdays) {
		String _id = MysqlAPI.string(String.valueOf(activeid));
		String _newdays = MysqlAPI.string(String.valueOf(newdays));

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("UPDATE ws_compras_active SET ws_compras_active.days = " + _newdays + " " + "WHERE " + "ws_compras_active.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: updateActiveDays (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}

	public int updateActiveLastUpdate(Integer activeid, String date) {
		String _id = MysqlAPI.string(String.valueOf(activeid));
		String _date = MysqlAPI.string(date);

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("UPDATE ws_compras_active SET ws_compras_active.lastupdate = '" + _date + "' " + "WHERE " + "ws_compras_active.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: updateActiveLastLogin (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}

	public int updateActiveDaysItemToLifetime(Integer activeid) {
		String _id = MysqlAPI.string(String.valueOf(activeid));

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("UPDATE ws_compras_active SET ws_compras_active.type = 0, ws_compras_active.days = 0 WHERE ws_compras_active.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: updateActiveDaysItemToLifetime (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}

	public int createActive(UUID uuid, Integer itemid, String itemname, String groupname, Integer days, Integer quant, Integer order) {
		String _uuid = MysqlAPI.string(String.valueOf(uuid).replace("-", ""));
		String _itemid = MysqlAPI.string(String.valueOf(itemid));
		String _itemname = MysqlAPI.string(String.valueOf(itemname));
		String _groupname = MysqlAPI.string(String.valueOf(groupname));
		String _days = MysqlAPI.string(String.valueOf(days));
		String _quant = MysqlAPI.string(String.valueOf(quant));
		String _order = MysqlAPI.string(String.valueOf(order));

		Integer _query = Integer.valueOf(0);

		int type = 1;
		if (Integer.valueOf(_days).intValue() == 0) {
			type = 0;
		}
		int __days = Integer.valueOf(_days).intValue() * Integer.valueOf(_quant).intValue();
		_days = String.valueOf(__days);
		String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
		try {
			_query = Integer.valueOf(this.app.getMysql().update("INSERT INTO ws_compras_active (ws_compras_active.uuid, ws_compras_active.order, ws_compras_active.created, ws_compras_active.lastupdate, ws_compras_active.item_id, ws_compras_active.item_name, ws_compras_active.item_groupname, ws_compras_active.type, ws_compras_active.days) VALUES ('" + _uuid + "', " + "" + _order + ", " + "'" + date + "', " + "'" + date + "', " + "" + _itemid + ", " + "'" + _itemname + "', " + "'" + _groupname + "', " + "" + type + ", " + "" + _days + " " + ")"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: createActive (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}

	public int deleteActiveByID(Integer activeid) {
		String _id = MysqlAPI.string(String.valueOf(activeid));

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("DELETE FROM ws_compras_active WHERE ws_compras_active.id = " + _id + " " + "AND ws_compras_active.type = 1 " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: deleteActiveByID (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}

	public Integer getGroupLength(String groupname) {
		int query = 0;
		String _groupname = MysqlAPI.string(groupname);
		try {
			ResultMap _query = this.app.getMysql().query("SELECT ws_compras_active.id FROM ws_compras_active WHERE ws_compras_active.item_groupname = '" + _groupname + "' " + "ORDER BY " + "ws_compras_active.id ASC ");
			query = _query.getFetchSize();
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getGroupLength (OrdersModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}
}
