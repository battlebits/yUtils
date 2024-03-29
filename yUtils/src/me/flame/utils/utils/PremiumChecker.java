package me.flame.utils.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import me.flame.utils.Main;

public class PremiumChecker {

	public static boolean isPremium(String userName) {
		boolean isPremium = false;
		try {
			URL url = new URL("https://minecraft.net/haspaid.jsp?user=" + userName);
			InputStream stream = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			if (br.readLine().equals("true"))
				isPremium = true;
			br.close();
			stream.close();
		} catch (Exception e) {
			Main.getPlugin().getLogger().warning("Erro ao saber se Usu�rio " + userName + " � Premium!");
		}
		return isPremium;
	}

	// http://wiki.vg/Mojang_API
}
