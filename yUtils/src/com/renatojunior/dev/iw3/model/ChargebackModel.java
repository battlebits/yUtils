package com.renatojunior.dev.iw3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.renatojunior.dev.iw3.classes.Application;

public class ChargebackModel {
	private final Application app;

	public ChargebackModel(Application app) {
		this.app = app;
	}

	public List<String[]> getPlayerChargebacksByUUID(UUID player) {
		List<String[]> query = new ArrayList<>();
		String uuid = this.app.getMysql().string(player.toString().replace("-", ""));
		try {
			ResultSet _query = this.app.getMysql().query("SELECT ws_chargeback.id, ws_chargeback.uuid, ws_chargeback.orderid FROM ws_chargeback WHERE ws_chargeback.uuid = '" + uuid + "' " + "ORDER BY " + "ws_chargeback.id DESC");
			while (_query.next()) {
				String[] result = { "", "", "" };
				result[0] = _query.getString("id");
				result[1] = _query.getString("uuid");
				result[2] = _query.getString("orderid");
				query.add(result);
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getPlayerChargeBacksByUUID (ChargebackModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public int deleteChargebackByID(Integer cbid) {
		String _id = this.app.getMysql().string(String.valueOf(cbid));

		Integer _query = Integer.valueOf(0);
		try {
			_query = Integer.valueOf(this.app.getMysql().update("DELETE FROM ws_chargeback WHERE ws_chargeback.id = " + _id + " " + "LIMIT 1"));
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: deleteChargebackByID (ChargebackModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return _query.intValue();
	}
}
