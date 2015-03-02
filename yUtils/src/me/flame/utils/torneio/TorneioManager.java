package me.flame.utils.torneio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.scheduler.BukkitRunnable;

public class TorneioManager extends Management {

	private List<UUID> participantes;

	public TorneioManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		participantes = new ArrayList<>();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Torneio`;");
					ResultSet result = stmt.executeQuery();
					participantes.clear();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUID(result.getString("uuid"));
						participantes.add(uuid);
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel carregar os participantes do torneio");
				}
			}
		}.runTaskTimerAsynchronously(getPlugin(), 5, 20 * 60 * 10);
	}

	@Override
	public void onDisable() {
		participantes.clear();
	}

	public boolean isParticipante(UUID uuid) {
		return participantes.contains(uuid);
	}
}
