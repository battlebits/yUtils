package me.flame.utils.permissions.enums;

public enum Group {
	NORMAL, 
	LIGHT("flame.vip.light"), 
	PREMIUM("flame.vip.premium"), 
	ULTIMATE("flame.vip.ultimate"), 
	YOUTUBER("flame.vip.youtuber"), 
	HELPER("flame.mod.helper"), 
	STAFF("flame.mod.staff"), 
	TRIAL("flame.mod.trial"), 
	MOD("flame.mod.full"), 
	ADMIN("flame.mod.admin"), 
	DONO("flame.dono");

	private String[] permissions;

	private Group(String... permissions) {
		this.permissions = permissions;
	}

	public String[] getPermissions() {
		return permissions;
	}
}
