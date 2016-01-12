package com.renatojunior.dev.iw3.constructor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultMap {

	private Iterator<Map<String, String>> iterator;
	private Map<String, String> current;
	private int fetchSize;
	public ResultMap(ResultSet set) {
		List<Map<String, String>> list = new ArrayList<>();
		try {
			ResultSetMetaData data = set.getMetaData();
			while (set.next()) {
				Map<String, String> map = new HashMap<>();
				for (int i = 1; i <= data.getColumnCount(); i++) {
					try {
						String key = data.getColumnName(i);
						String value = set.getString(key);
						map.put(key, value);
					} catch (Exception e) {
					}
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		fetchSize = list.size();
		iterator = list.iterator();
	}

	public boolean next() {
		if(current != null) {
			iterator.remove();
		}
		if (!iterator.hasNext()) {
			return false;
		}
		current = iterator.next();
		return true;
	}
	
	public String getString(String str) {
		return current.get(str);
	}

	public int getFetchSize() {
		return fetchSize;
	}

}
