package me.flame.utils.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Utils {
	public static boolean newProtocol = false;
	public static String version;

	static {
		String className = "net.minecraft.server.v1_7_R4.MinecraftServer";
		try {
			Class.forName(className);
			version = "v1_7_R4.";
		} catch (ClassNotFoundException e) {
			version = "v1_8_R1.";
		}
	}

	public static Object newPacketPlayOutEntityDestroy(int id) {
		Class<?> PacketPlayOutEntityDestroy = getCraftClass("PacketPlayOutEntityDestroy");
		Object packet = null;
		try {
			packet = PacketPlayOutEntityDestroy.newInstance();
			Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, new int[] { id });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public static Object newPacketPlayOutPlayerInfo(PlayerInfoAction action, Object obj) {
		Class<?> classe = getCraftClass("PacketPlayOutPlayerInfo");
		Object packet = null;
		if (version.equals("v1_7_R4.")) {
			Method method = null;
			switch (action) {
			case ADD_PLAYER:
				method = getMethod(classe, "addPlayer");
				break;
			case REMOVE_PLAYER:
				method = getMethod(classe, "removePlayer");
				break;
			case UPDATE_DISPLAY_NAME:
				method = getMethod(classe, "updateDisplayName");
				break;
			case UPDATE_GAME_MODE:
				method = getMethod(classe, "updateGamemode");
				break;
			case UPDATE_LATENCY:
				method = getMethod(classe, "updatePing");
				break;
			default:
				break;
			}
			try {
				packet = method.invoke(null, obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Class<?> enume = getCraftClass("EnumPlayerInfoAction");
				packet = classe.getConstructor(Field.class, Object.class).newInstance(enume.getDeclaredField(action.toString()), obj);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return packet;
	}

	public static Object newPacketPlayOutNamedEntitySpawn(Object obj) {
		Class<?> classe = getCraftClass("PacketPlayOutNamedEntitySpawn");
		if (classe == null)
			return null;
		Object packet = null;
		try {
			for (Constructor<?> construct : classe.getConstructors()) {
				if (construct.getParameterCount() > 0) {
					packet = construct.newInstance(obj);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public static void sendPacket(Player p, Object packet) {
		try {
			Object nmsPlayer = getHandle(p);
			Field con_field = nmsPlayer.getClass().getDeclaredField("playerConnection");
			Object con = con_field.get(nmsPlayer);
			Method packet_method = con.getClass().getMethod("sendPacket", getCraftClass("Packet"));
			packet_method.invoke(con, packet);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getCraftClass(String ClassName) {
		String className = "net.minecraft.server." + version + ClassName;
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Class<?> getGsonClass() {
		String str = "com.google.gson.Gson";
		if (version.equals("v1_7_R4.")) {
			str = "org.bukkit.craftbukkit.libs." + str;
		}
		return getClass(str);
	}

	public static Class<?> getJsonObjectClass() {
		String str = "com.google.gson.JsonObject";
		if (version.equals("v1_7_R4.")) {
			str = "org.json.simple.JSONObject";
		}
		return getClass(str);
	}

	public static Class<?> getGameProfileClass() {
		if (version.equals("v1_7_R4.")) {
			return getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
		}
		return getClass("com.mojang.authlib.GameProfile");
	}

	public static Class<?> getPropertyClass() {
		if (version.equals("v1_7_R4.")) {
			return getClass("net.minecraft.util.com.mojang.authlib.properties.Property");
		}
		return getClass("com.mojang.authlib.properties.Property");
	}

	public static Class<?> getClass(String ClassName) {
		Class<?> c = null;
		try {
			c = Class.forName(ClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Object getHandle(World world) {
		Object nms_entity = null;
		Method entity_getHandle = getMethod(world.getClass(), "getHandle");
		try {
			nms_entity = entity_getHandle.invoke(world);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nms_entity;
	}

	public static Object getHandle(Entity entity) {
		Object nms_entity = null;
		Method entity_getHandle = getMethod(entity.getClass(), "getHandle");
		try {
			nms_entity = entity_getHandle.invoke(entity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nms_entity;
	}

	public static Field getField(Class<?> cl, String field_name) {
		try {
			Field field = cl.getField(field_name);
			return field;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) {
				return m;
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method, Integer args) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method) && args.equals(new Integer(m.getParameterTypes().length))) {
				return m;
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> cl, String method) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;

		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}

		return equal;
	}

	public static enum PlayerInfoAction {

		ADD_PLAYER(0), UPDATE_GAME_MODE(1), UPDATE_LATENCY(2), UPDATE_DISPLAY_NAME(3), REMOVE_PLAYER(4);

		private int id;

		private PlayerInfoAction(int i) {
			this.id = i;
		}

		public int getId() {
			return id;
		}
	}
}
