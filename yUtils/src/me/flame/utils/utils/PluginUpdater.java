package me.flame.utils.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.Plugin;

public class PluginUpdater implements Runnable {

	private boolean running;
	private Plugin plugin;
	private String pluginName;
	private String versaoAtual;
	private String versaoUpdate;
	private String downloadURL;
	private boolean needUpdate = true;

	public PluginUpdater(Plugin plugin) {
		this.plugin = plugin;
		this.pluginName = plugin.getName();
		versaoAtual = plugin.getDescription().getVersion();
		downloadURL = "http://battlebits.com.br/hkjaosdja3sd/update/" + pluginName + "/" + pluginName + ".jar";
	}

	@Override
	public void run() {
		if (!needUpdate)
			return;
		if (running)
			return;
		running = true;
		String urlStr = "http://battlebits.com.br/hkjaosdja3sd/update/" + pluginName + "/version.txt";
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				versaoUpdate = inputLine;
			}
			if (versaoAtual.equals(versaoUpdate)) {
				System.out.println("============================");
				System.out.println("Plugin " + pluginName + " ja esta atualizado");
				System.out.println("============================");
				running = false;
				return;
			}
			downloadUpdate();
		} catch (Exception e) {
			System.out.println("============================");
			System.out.println("Erro ao procurar atualização de " + pluginName);
			System.out.println("============================");
			e.printStackTrace();
			running = false;
		}
	}

	private void downloadUpdate() {
		try {
			File to = new File(plugin.getServer().getUpdateFolderFile(), pluginName + ".jar");
			File tmp = new File(to.getPath() + ".au");
			if (!tmp.exists()) {
				plugin.getServer().getUpdateFolderFile().mkdirs();
				tmp.createNewFile();
			}
			URL url = new URL(downloadURL);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(tmp);
			byte[] buffer = new byte[4096];
			int fetched;
			while ((fetched = is.read(buffer)) != -1)
				os.write(buffer, 0, fetched);
			is.close();
			os.flush();
			os.close();
			if (to.exists())
				to.delete();
			tmp.renameTo(to);
			System.out.println("============================");
			System.out.println("Atualizacao de " + pluginName + " baixada com sucesso e terá efeito na proxima reinicialização");
			System.out.println("============================");
			needUpdate = false;
			running = false;
		} catch (Exception e) {
			System.out.println("============================");
			System.out.println("Erro ao tentar baixar update de " + pluginName);
			System.out.println("============================");
			e.printStackTrace();
			running = false;
		}
	}

}
