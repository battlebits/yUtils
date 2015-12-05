package com.renatojunior.dev.iw3.classes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.renatojunior.dev.iw3.constructor.ResultMap;

public class MysqlAPI {
	private final Application app;

	public MysqlAPI(Application app) {
		this.app = app;
	}

	public Connection connect() throws SQLException, ClassNotFoundException {
		String sql_host = this.app.getPlugin().host;
		String sql_port = this.app.getPlugin().port;
		String sql_name = "iw3";
		String sql_user = this.app.getPlugin().user;
		String sql_pass = this.app.getPlugin().password;
		MySQL mysql = new MySQL(this.app.getPlugin(), sql_host, sql_port, sql_name, sql_user, sql_pass);
		return mysql.openConnection();
	}

	public void close() throws SQLException, ClassNotFoundException {
		if (is_connected()) {
			this.app.getConnection().close();
		}
	}

	public boolean is_connected() throws SQLException {
		return !this.app.getConnection().isClosed();
	}

	public ResultMap query(String code) throws SQLException, ClassNotFoundException {
		if (!is_connected()) {
			connect();
		}
		Statement statement = this.app.getConnection().createStatement();
		ResultSet set = statement.executeQuery(code);
		ResultMap map = new ResultMap(set);
		set.close();
		statement.close();
		return map;
	}

	public int update(String code) throws SQLException, ClassNotFoundException {
		if (!is_connected()) {
			connect();
		}
		Statement statement = this.app.getConnection().createStatement();
		int ret = statement.executeUpdate(code);
		
		statement.close();
		
		return ret;
	}

	public static String string(String string) {
		return string.replaceAll("'", "");
	}
}
