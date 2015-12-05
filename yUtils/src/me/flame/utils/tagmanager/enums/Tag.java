package me.flame.utils.tagmanager.enums;

import org.bukkit.ChatColor;

public enum Tag {
	DONO("adono", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DONO " + ChatColor.DARK_RED), 
	ADMIN("badmin", ChatColor.RED.toString() + ChatColor.BOLD + "ADMIN " + ChatColor.RED), 
	MOD("cmod", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "MOD " + ChatColor.DARK_PURPLE), 
	TRIAL("dtrial", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "TRIAL " + ChatColor.DARK_PURPLE), 
	HELPER("ehelper", ChatColor.BLUE + "" + ChatColor.BOLD + "HELPER " + ChatColor.BLUE),
	STAFF("fstaff", ChatColor.YELLOW + "" + ChatColor.BOLD + "STAFF " + ChatColor.YELLOW), 
	BUILDER("gstaff", ChatColor.YELLOW + "" + ChatColor.BOLD + "BUILDER " + ChatColor.YELLOW), 
	DEV("hdev", ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "DEV " + ChatColor.DARK_AQUA), 
	YOUTUBER("iyoutuber", ChatColor.AQUA + "" + ChatColor.BOLD + "YOUTUBER " + ChatColor.AQUA), 
	ULTIMATE("jultimate", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "ULTIMATE " + ChatColor.LIGHT_PURPLE), 
	PREMIUM("kpremium", ChatColor.GOLD + "" + ChatColor.BOLD + "PREMIUM " + ChatColor.GOLD), 
	LIGHT("llight", ChatColor.GREEN + "" + ChatColor.BOLD + "LIGHT " + ChatColor.GREEN), 
	TORNEIO("mtorneio", ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "TORNEIO " + ChatColor.DARK_BLUE),
	WINNER("nwinner", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "WINNER " + ChatColor.DARK_GREEN),
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
