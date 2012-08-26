package org.blockface.virtualshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import net.milkbowl.vault.economy.Economy;

import org.blockface.virtualshop.commands.*;
import org.blockface.virtualshop.managers.ConfigManager;
import org.blockface.virtualshop.persistance.StockTable;
import org.blockface.virtualshop.persistance.TransactionsTable;
import org.blockface.virtualshop.util.ItemDb;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

public class VirtualShop extends JavaPlugin {

	public static Economy economy = null;
	public static EbeanServer es;

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
		setupDatabase();
		setupEconomy();
		getCommand("buy").setExecutor(new BuyCommand(this));
		getCommand("sell").setExecutor(new SellCommand(this));
		getCommand("find").setExecutor(new FindCommand(this));
		getCommand("sales").setExecutor(new SalesCommand(this));
		getCommand("stock").setExecutor(new StockCommand(this));
		getCommand("virtualshop").setExecutor(new HelpCommand());
		getCommand("cancel").setExecutor(new CancelCommand(this));
		es = getDatabase();
	}

	// Set up Vault economy
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
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

	private void setupDatabase() {
		try {
			File ebeans = new File("ebean.properties");
			if (!ebeans.exists()) {
				try {
					ebeans.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			getDatabase().find(StockTable.class).findRowCount();
			getDatabase().find(TransactionsTable.class).findRowCount();
		} catch (PersistenceException ex) {
			this.getLogger().log(Level.INFO, "Installing database.");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(StockTable.class);
		list.add(TransactionsTable.class);
		return list;
	}
}