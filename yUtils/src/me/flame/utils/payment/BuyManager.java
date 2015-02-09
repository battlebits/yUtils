package me.flame.utils.payment;

import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;

public class BuyManager extends Management {
	private HashMap<UUID, Long> expires;

	public BuyManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		expires = new HashMap<>();

	}

	@Override
	public void onDisable() {

	}

}
