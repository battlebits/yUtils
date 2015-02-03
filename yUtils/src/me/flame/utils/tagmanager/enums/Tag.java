package me.flame.utils.tagmanager.enums;

import org.bukkit.ChatColor;

public enum Tag {
	DONO("adono", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DONO " + ChatColor.DARK_RED), 
	ADMIN("badmin", ChatColor.RED.toString() + ChatColor.BOLD + "ADMIN " + ChatColor.RED), 
	MOD("cmod", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "MOD " + ChatColor.DARK_PURPLE), 
	TRIAL("dtrial", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "TRIAL " + ChatColor.DARK_PURPLE), 
	HELPER("ehelper", ChatColor.BLUE + "" + ChatColor.BOLD + "HELPER " + ChatColor.BLUE),
	STAFF("fstaff", ChatColor.YELLOW + "" + ChatColor.BOLD + "STAFF " + ChatColor.YELLOW), 
	YOUTUBER("gyoutuber", ChatColor.AQUA + "" + ChatColor.BOLD + "YOUTUBER " + ChatColor.AQUA), 
	ULTIMATE("hultimate", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "ULTIMATE " + ChatColor.LIGHT_PURPLE), 
	PREMIUM("ipremium", ChatColor.GOLD + "" + ChatColor.BOLD + "PREMIUM " + ChatColor.GOLD), 
	LIGHT("jlight", ChatColor.GREEN + "" + ChatColor.BOLD + "LIGHT " + ChatColor.GREEN), 
	NORMAL("znormal", ChatColor.GRAY + "");

	private String teamName;
	private String prefix;

	private Tag(String teamName, String prefix) {
		this.teamName = teamName;
		this.prefix = prefix;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getPrefix() {
		return prefix;
	}
}
