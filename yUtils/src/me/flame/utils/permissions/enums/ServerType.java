package me.flame.utils.permissions.enums;

public enum ServerType {
	HUNGERGAMES, BATTLECRAFT, SKYWARS, LOBBY, RAID, GARTICCRAFT, TESTSERVER, NONE;

	@Override
	public String toString() {
		return getServerName(super.toString().toLowerCase());
	}

	private static String getServerName(String server) {
		String serverName = server;
		char[] stringArray = serverName.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		serverName = new String(stringArray);
		return serverName;
	}
}
