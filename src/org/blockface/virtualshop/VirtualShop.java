package org.blockface.virtualshop;

import java.io.IOException;
import java.util.logging.Level;

import org.blockface.virtualshop.commands.*;
import org.blockface.virtualshop.managers.ConfigManager;
import org.blockface.virtualshop.util.ItemDb;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VirtualShop extends JavaPlugin {

	public void onDisable() {
	}

	public void onEnable() {
		Chatty.Initialize(this);
		ConfigManager.Initialize(this);
		try {
			ItemDb.load(getDataFolder(), "items.csv");
		} catch (IOException e) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		getCommand("buy").setExecutor(new BuyCommand(this));
		getCommand("sell").setExecutor(new SellCommand(this));
		getCommand("find").setExecutor(new FindCommand(this));
		getCommand("sales").setExecutor(new SalesCommand(this));
		getCommand("stock").setExecutor(new StockCommand(this));
		getCommand("virtualshop").setExecutor(new HelpCommand(this));
		getCommand("cancel").setExecutor(new CancelCommand(this));
	}
	
	public boolean console(CommandSender sender) {
		if (sender instanceof Player) {
			return false;
		}
		return true;
	}
	
	public boolean hasPerm(CommandSender sender, String label,
			boolean consoleUse) {
		boolean perm = sender.hasPermission("virtualshop." + label);

		if (this.console(sender)) {
			if (consoleUse)
				return true;

			this.logger(Level.INFO, "This command cannot be used in console.");
			return false;
		} else {
			if (sender.isOp())
				return true;

			return perm;
		}
	}
	
	public void logger(Level l, String s) {
		this.getLogger().log(l, s);
	}
}