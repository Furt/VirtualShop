package org.blockface.virtualshop.commands;

import java.util.List;

import org.blockface.virtualshop.Chatty;
import org.blockface.virtualshop.VirtualShop;
import org.blockface.virtualshop.managers.DatabaseManager;
import org.blockface.virtualshop.objects.Transaction;
import org.blockface.virtualshop.util.Numbers;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SalesCommand implements CommandExecutor {
	private VirtualShop plugin;

	public SalesCommand(VirtualShop instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!plugin.hasPerm(sender, label, true)) {
			Chatty.NoPermissions(sender);
			return true;
		}

		int start = 1;

		List<Transaction> transactions = DatabaseManager.GetTransactions();
		if (args.length > 0)
			start = Numbers.ParseInteger(args[1]).intValue();

		if (start < 1) {
			String search = args[1];
			if (args.length > 1)
				start = Numbers.ParseInteger(args[2]).intValue();
			if (start < 0)
				start = 1;
			start = (start - 1) * 9;
			transactions = DatabaseManager.GetTransactions(args[0], search);
		} else {
			start = (start - 1) * 9;
		}

		int page = start / 9 + 1;
		int pages = transactions.size() / 9 + 1;
		if (page > pages) {
			start = 0;
			page = 1;
		}

		sender.sendMessage(ChatColor.DARK_GRAY + "---------------"
				+ ChatColor.GRAY + "Page (" + ChatColor.RED + page
				+ ChatColor.GRAY + " of " + ChatColor.RED + pages
				+ ChatColor.GRAY + ")" + ChatColor.DARK_GRAY
				+ "---------------");
		int count = 0;
		for (Transaction t : transactions) {
			if (count == start + 9)
				break;
			if (count >= start) {
				sender.sendMessage(Chatty.FormatTransaction(t));
			}
			count++;
		}
		return true;
	}

}