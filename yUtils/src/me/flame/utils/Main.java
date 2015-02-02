package me.flame.utils;

import me.flame.utils.permissions.PermissionManager;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public String mysql_url = "localhost";
	public String mysql_user = "pagseguro";
	public String mysql_pass = "password";

	@Override
	public void onEnable() {
		new PermissionManager().onEnable(this);
	}

}
