package com.rabbitcompany.utils;

import org.bukkit.Bukkit;

public enum Version {

	MC1_19("1.19", 11),
	MC1_18("1.18", 10),
	MC1_17("1.17", 9),
	MC1_16("1.16", 8),
	MC1_15("1.15", 7),
	MC1_14("1.14", 6),
	MC1_13("1.13", 5),
	MC1_12("1.12", 4),
	MC1_11("1.11", 3),
	MC1_10("1.10", 2),
	MC1_9("1.9", 1),
	MC1_8("1.8", 0),
	UNKNOWN("Unknown", -1);

	private final String versionID;
	private final int value;

	Version(String versionID, int value) {
		this.versionID = versionID;
		this.value = value;
	}

	public static Version getVersion() {
		for (Version version : values()) {
			if (Bukkit.getBukkitVersion().contains(version.versionID)) {
				return version;
			}
		}
		return Version.UNKNOWN;
	}

	public static boolean isAtLeast(Version version) {
		return getVersion().value >= version.value;
	}

	public static boolean isAtMost(Version version) {
		return getVersion().value <= version.value;
	}

	public static boolean isVersion(Version version) {
		return getVersion().value == version.value;
	}

}
