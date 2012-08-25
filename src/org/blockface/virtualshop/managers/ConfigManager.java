package org.blockface.virtualshop.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	private static FileConfiguration config;

	public static void Initialize(Plugin plugin) {
		config = plugin.getConfig();
		config.getDefaults();
		BroadcastOffers();
		plugin.saveConfig();
	}

	public static Boolean BroadcastOffers() {
		return Boolean.valueOf(config.getBoolean("broadcast-offers", true));
	}
}