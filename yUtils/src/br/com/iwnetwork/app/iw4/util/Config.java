package br.com.iwnetwork.app.iw4.util;

import static br.com.iwnetwork.app.iw4.IW4.getRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.ServerType;

public class Config {

	private final String location = "plugins/iw4/init.conf";
	private File file;
	private HashMap<String, String> defaultProperties;
	private Properties properties;

	public Config() {
		this.file = new File(location);

		this.defaultProperties = new HashMap<>();
		this.properties = new Properties();

		File dir = new File("plugins/iw4");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		load();
		assignDefault();
	}

	private void load() {
		try {
			if (!this.file.exists()) {
				this.file.createNewFile();
			}
			this.properties.load(new FileInputStream(location));
		} catch (IOException e) {
			getRegistry().logger().log(e);
		}
	}

	private void assignDefault() {
		Boolean toSave = Boolean.valueOf(false);

		this.defaultProperties.put("api_auth_url", "http://loja.battlebits.com.br/api");
		this.defaultProperties.put("api_auth_key", "3tPtWGqdW8L0X1Y22v5jIsLZx3MIo0WWNLCHckLlAyvblePONfN3SdluAE4Infvczy0Vvkb4VYG3biU7OswtXXROmg5YXKGdiTGShrp3gMi3QPaPLOJ8g80I746MaU6jH5YRfMOqssQaT6H6pKlMzgptBlrYfEG35fcFxgGaIpfCt8aCqIeqCMuw0WXlVls3ICLWZbx0jj2JUQjy8RZpNslrLpTZaPV4cVviL2Wv6m41bV4osrRLdQZ7zJ1VkwBd");
		this.defaultProperties.put("api_auth_version", "v1");
		this.defaultProperties.put("api_config_server", Main.getPlugin().getServerType() == ServerType.HUNGERGAMES ? "hungergames" : Main.getPlugin().getServerType() == ServerType.BATTLECRAFT ? "battlecraft" : "network");
		this.defaultProperties.put("cmd_packages", "pacotes");
		this.defaultProperties.put("debug_mode", "false");
		this.defaultProperties.put("text_product", "Produtos");
		this.defaultProperties.put("text_commands", "Comandos");
		this.defaultProperties.put("text_days_remaining", "Dias Restantes");
		this.defaultProperties.put("text_showing_pkg", "Mostrando {0} pacotes");
		this.defaultProperties.put("text_no_pkg", "Nenhum pacote encontrado");
		for (Map.Entry<String, String> entry : this.defaultProperties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (!this.properties.containsKey(key)) {
				this.properties.setProperty(key, value);

				toSave = Boolean.valueOf(true);
			}
		}
		if (toSave.booleanValue()) {
			saveSettings();
		}
	}

	private void saveSettings() {
		try {
			this.properties.store(new FileOutputStream(location), "iw4 Plugin");
		} catch (FileNotFoundException e) {
			getRegistry().logger().log(e);
		} catch (IOException e) {
			getRegistry().logger().log(e);
		}
	}

	public boolean getBoolean(String key) {
		if (this.properties.containsKey(key)) {
			return Boolean.valueOf(getString(key)).booleanValue();
		}
		throw new RuntimeException("Settings key '" + key + "' not found in the settings.conf file.");
	}

	public int getInt(String key) {
		if (this.properties.containsKey(key)) {
			return Integer.valueOf(getString(key)).intValue();
		}
		throw new RuntimeException("Settings key '" + key + "' not found in the settings.conf file.");
	}

	public String getString(String key) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}
		throw new RuntimeException("Settings key '" + key + "' not found in the settings.conf file.");
	}

	public void setString(String key, String value) {
		this.properties.setProperty(key, value);

		saveSettings();
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

}
