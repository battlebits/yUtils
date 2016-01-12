package me.flame.utils.ranking.constructors;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.ranking.RankingManager;
import me.flame.utils.ranking.enums.Rank;

import org.bukkit.scheduler.BukkitRunnable;

public class Account {
	private UUID uuid;
	private int xp;
	private int money;
	private int fichas;
	private RankingManager manager;
	private ReentrantLock lock;

	public Account(RankingManager manager, UUID uuid) {
		this(manager, uuid, 0, 0, 0);
	}

	public Account(RankingManager manager, UUID uuid, int xp, int money, int fichas) {
		this.manager = manager;
		this.uuid = uuid;
		this.xp = xp;
		this.money = money;
		this.fichas = fichas;
		this.lock = new ReentrantLock();
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

	public ReentrantLock getLock() {
		return lock;
	}

	public int addMoney(int money) {
		int multiplier = 1 + getLiga().ordinal() + (Main.getPlugin().getPermissionManager().hasGroupPermission(uuid, Group.ULTIMATE) ? 1 : 0);
		int plus = money * multiplier;
		this.money += plus;
		save();
		return plus;
	}

	public int removeMoney(int money) {
		this.money -= money;
		if (this.money < 0)
			this.money = 0;
		save();
		return this.money;
	}

	public int addXp(int xp) {
		int multiplier = 1 + (Main.getPlugin().getPermissionManager().hasGroupPermission(uuid, Group.ULTIMATE) ? 1 : 0);
		int plus = xp * multiplier;
		this.xp += plus;
		save();
		return plus;
	}

	public int removeXp(int xp) {
		this.xp -= xp;
		if (this.xp < 0)
			this.xp = 0;
		save();
		return this.xp;
	}

	public int addFichas(int fichas) {
		this.fichas += fichas;
		save();
		return this.fichas;
	}

	public int removeFichas(int fichas) {
		this.fichas -= fichas;
		if (this.fichas < 0)
			this.fichas = 0;
		save();
		return this.fichas;
	}

	private void save() {
		this.lock.lock();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					manager.saveAccount(Account.this);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					new BukkitRunnable() {
						@Override
						public void run() {
							Account.this.lock.unlock();
						}
					}.runTask(manager.getPlugin());
				}
			}
		}.runTaskAsynchronously(manager.getPlugin());
	}
}
