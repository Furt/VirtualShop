package org.blockface.virtualshop.commands;

import org.blockface.virtualshop.VirtualShop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
	private VirtualShop plugin;

	public HelpCommand(VirtualShop instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		sender.sendMessage(ChatColor.GRAY + "|-------------" + ChatColor.DARK_GREEN + "VirtualShop" + ChatColor.GRAY + "-------------|");
	    sender.sendMessage(ChatColor.RED + "/buy " + ChatColor.GOLD + "<amount> " + ChatColor.BLUE + "<item> " + ChatColor.YELLOW + "[maxprice]" + ChatColor.WHITE + " - buy items.");
	    sender.sendMessage(ChatColor.RED + "/sell " + ChatColor.GOLD + "<amount> " + ChatColor.BLUE + "<item> " + ChatColor.YELLOW + "<price>" + ChatColor.WHITE + " - sell items.");
	    sender.sendMessage(ChatColor.RED + "/cancel " + ChatColor.BLUE + "<item> " + ChatColor.WHITE + " - remove item from shop.");
	    sender.sendMessage(ChatColor.RED + "/find " + ChatColor.BLUE + "<item> " + ChatColor.WHITE + ChatColor.LIGHT_PURPLE + "[page] " + ChatColor.WHITE + " - find offers for the item.");
	    sender.sendMessage(ChatColor.RED + "/stock " + ChatColor.AQUA + "[player] " + ChatColor.LIGHT_PURPLE + "[page] " + ChatColor.WHITE + " - browse offers.");
	    sender.sendMessage(ChatColor.RED + "/sales " + ChatColor.AQUA + "[player] " + ChatColor.LIGHT_PURPLE + "[page] " + ChatColor.WHITE + " - view transaction log.");
		return true;
	}

}
