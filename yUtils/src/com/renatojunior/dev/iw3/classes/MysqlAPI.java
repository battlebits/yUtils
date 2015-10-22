package com.renatojunior.dev.iw3.classes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlAPI {
	private final Application app;

	public MysqlAPI(Application app) {
		this.app = app;
	}

	public Connection connect() throws SQLException, ClassNotFoundException {
		String sql_host = this.app.getPlugin().host;
		String sql_port = this.app.getPlugin().port;
		String sql_name = "IW3";
		String sql_user = this.app.getPlugin().user;
		String sql_pass = this.app.getPlugin().password;
		MySQL mysql = new MySQL(this.app.getPlugin(), sql_host, sql_port, sql_name, sql_user, sql_pass);
		return mysql.openConnection();
	}

	public void close() throws SQLException, ClassNotFoundException {
		if (is_connected()) {
			this.app.getConnection(new Connection[0]).close();
		}
	}

	public boolean is_connected() throws SQLException {
		return this.app.getConnection(new Connection[0]).isClosed();
	}

	public ResultSet query(String code) throws SQLException, ClassNotFoundException {
		if (!is_connected()) {
			this.app.getConnection(new Connection[] { connect() });
		}
		Statement statement = this.app.getConnection(new Connection[0]).createStatement();
		return statement.executeQuery(code);
	}

	public int update(String code) throws SQLException, ClassNotFoundException {
		if (!is_connected()) {
			this.app.getConnection(new Connection[] { connect() });
		}
		Statement statement = this.app.getConnection(new Connection[0]).createStatement();
		return statement.executeUpdate(code);
	}

	public String string(String string) {
		return string.replaceAll("'", "");
	}
}
