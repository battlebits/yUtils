package com.renatojunior.dev.iw3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.renatojunior.dev.iw3.classes.Application;

public class ProductsModel {
	private final Application app;

	public ProductsModel(Application app) {
		this.app = app;
	}

	public String[] getProductByID(Integer productid) {
		String[] query = null;
		String id = this.app.getMysql().string(productid.toString());
		try {
			ResultSet _query = this.app.getMysql().query("SELECT ws_produtos.name, ws_produtos.groupname, ws_produtos.duracao, ws_produtos.onstart, ws_produtos.onstop, ws_produtos.onchargeback FROM ws_produtos WHERE ws_produtos.id = " + id + " " + "AND ws_produtos.active = 1 " + "ORDER BY " + "ws_produtos.id DESC " + "LIMIT 1");
			while (_query.next()) {
				String[] pre_query = { "", "", "", "", "", "" };
				pre_query[0] = _query.getString("name");
				pre_query[1] = _query.getString("groupname");
				pre_query[2] = _query.getString("duracao");
				pre_query[3] = _query.getString("onstart");
				pre_query[4] = _query.getString("onstop");
				pre_query[5] = _query.getString("onchargeback");
				query = pre_query;
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getProductByID (ProductsModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public List<String> getProductServers(Integer productid) {
		List<String> query = new ArrayList<>();
		String _id = this.app.getMysql().string(productid.toString());
		try {
			ResultSet _query = this.app.getMysql().query("SELECT ws_produtos.servers FROM ws_produtos WHERE ws_produtos.id = " + _id + " " + "LIMIT 1");
			while (_query.next()) {
				String servers = _query.getString("servers").replace(".", "");
				String[] _servers_raw = servers.split("-");
				for (String _server : _servers_raw) {
					if (!"*".equals(_server)) {
						String servername = getServerNameByID(Integer.valueOf(_server));
						if (servername != null) {
							query.add(servername);
						}
					} else {
						query.add("*");
					}
				}
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getProductServers (ProductsModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}

	public String getServerNameByID(Integer id) {
		String query = null;
		String _serverid = this.app.getMysql().string(String.valueOf(id));
		try {
			ResultSet _query = this.app.getMysql().query("SELECT ws_servers.groupname FROM ws_servers WHERE ws_servers.id = " + _serverid + " " + "LIMIT 1");
			while (_query.next()) {
				query = _query.getString("groupname");
			}
		} catch (SQLException | ClassNotFoundException err) {
			this.app.getChat().consoleMessage("&4[IW3][Error] &fQuery error: getServerNameByID (ProductsModel.java)");
			this.app.getChat().consoleMessage(err.toString());
		}
		return query;
	}
}
