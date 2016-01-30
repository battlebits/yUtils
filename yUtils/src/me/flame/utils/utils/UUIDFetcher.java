package me.flame.utils.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class UUIDFetcher {

	private static JsonParser parser = new JsonParser();
	private static final String profileURL = "https://api.mojang.com/users/profiles/minecraft";
	private static Cache<String, UUID> nameUUID = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS)
			.build(new CacheLoader<String, UUID>() {
				@Override
				public UUID load(String name) throws Exception {
					return loadFromMojang(name);
				}
			});

	private static UUID loadFromMojang(String name) throws Exception {
		UUID id = null;
		URL url = new URL(profileURL + "/" + name);
		InputStream is = url.openStream();
		InputStreamReader streamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
		JsonObject object = parser.parse(streamReader).getAsJsonObject();
		if (object.get("name").getAsString().equalsIgnoreCase(name)) {
			id = getUUIDFromString(object.get("id").getAsString());
		}
		streamReader.close();
		is.close();
		return id;
	}

	public static UUID getUUIDFromString(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-"
				+ id.substring(20, 32));
	}

	public static UUID getUUIDOf(String name) throws Exception {
		return nameUUID.get(name);
	}
}