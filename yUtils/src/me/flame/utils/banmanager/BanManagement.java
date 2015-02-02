package me.flame.utils.banmanager;

import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;

import org.bukkit.entity.Player;

public class BanManagement extends Management {
	private HashMap<UUID, Ban> banimentos;
	private HashMap<UUID, Mute> mutados;

	public BanManagement(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		this.banimentos = new HashMap<>();
		this.mutados = new HashMap<>();
		// TODO carregar banimentos
	}

	public boolean isBanned(Player player) {
		UUID uuid = player.getUniqueId();
		return banimentos.containsKey(uuid);
	}

	public Ban getBan(Player player) {
		if (!isBanned(player))
			return null;
		UUID uuid = player.getUniqueId();
		return banimentos.get(uuid);
	}

	public boolean isMuted(Player player) {
		UUID uuid = player.getUniqueId();
		return mutados.containsKey(uuid);
	}

	public Mute getMute(Player player) {
		if (!isMuted(player))
			return null;
		UUID uuid = player.getUniqueId();
		return mutados.get(uuid);
	}
}
