package me.flame.utils.permissions.enums;

import me.flame.utils.permissions.groups.*;

public enum Group {
	NORMAL(new Normal()), LIGHT(new Light()), PREMIUM(new Premium()), ULTIMATE(new Ultimate()), YOUTUBER(new Youtuber()), DEV(new Developer()), HELPER(new Helper()), STAFF(new Staff()), TRIAL(new Trial()), MOD(new Moderator()), ADMIN(new Administrator()), DONO(new Dono());

	private MainGroup group;

	private Group(MainGroup group) {
		this.group = group;
	}

	public MainGroup getGroup() {
		return group;
	}
}
