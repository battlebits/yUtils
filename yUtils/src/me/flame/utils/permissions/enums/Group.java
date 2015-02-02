package me.flame.utils.permissions.enums;

import me.flame.utils.permissions.groups.*;

public enum Group {
	NORMAL(Normal.class), LIGHT(Light.class), PREMIUM(Premium.class), ULTIMATE(Ultimate.class), YOUTUBER(Youtuber.class), HELPER(Helper.class), STAFF(Staff.class), TRIAL(Trial.class), MOD(Moderator.class), ADMIN(Administrator.class), DONO(Dono.class);

	private Class<? extends MainGroup> group;

	private Group(Class<? extends MainGroup> classe) {
		this.group = classe;
	}

	public MainGroup getGroup() {
		MainGroup groupe = null;
		try {
			groupe = group.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return groupe;
	}
}
