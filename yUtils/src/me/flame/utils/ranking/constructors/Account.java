package me.flame.utils.ranking.constructors;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.ranking.enums.Rank;

public class Account {
	private UUID uuid;
	private int xp;
	private int money;
	private int fichas;
	
	public Account(UUID uuid) {
		this(uuid, 0, 0, 0);
	}

	public Account(UUID uuid, int xp, int money, int fichas) {
		this.uuid = uuid;
		this.xp = xp;
		this.money = money;
		this.fichas = fichas;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getXp() {
		return xp;
	}

	public int getMoney() {
		return money;
	}

	public int getFichas() {
		return fichas;
	}

	public Rank getLiga() {
		return Rank.getLiga(xp);
	}

	public int addMoney(int money) {
		int multiplier = 1 + getLiga().ordinal() + (Main.getPlugin().getPermissionManager().hasGroupPermission(Bukkit.getPlayer(uuid), Group.ULTIMATE) ? 1 : 0);
		int plus = money * multiplier;
		this.money += plus;
		return plus;
	}
	
	public int removeMoney(int money) {
		this.money -= money;
		if(this.money < 0)
			this.money = 0;
		return this.money;
	}

	public int addXp(int xp) {
		int multiplier = 1 + (Main.getPlugin().getPermissionManager().hasGroupPermission(Bukkit.getPlayer(uuid), Group.ULTIMATE) ? 1 : 0);
		int plus = xp * multiplier;
		this.xp += plus;
		return plus;
	}
	
	public int removeXp(int xp) {
		this.xp -= xp;
		if(this.xp < 0)
			this.xp = 0;
		return this.xp;
	}
	
	public int addFichas(int fichas) {
		this.fichas += fichas;
		return this.fichas;
	}
	
}
