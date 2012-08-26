package org.blockface.virtualshop.commands;

import org.blockface.virtualshop.Chatty;
import org.blockface.virtualshop.VirtualShop;
import org.blockface.virtualshop.managers.DatabaseManager;
import org.blockface.virtualshop.objects.Offer;
import org.blockface.virtualshop.util.InventoryManager;
import org.blockface.virtualshop.util.ItemDb;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CancelCommand implements CommandExecutor {
	private VirtualShop plugin;

	public CancelCommand(VirtualShop instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!plugin.hasPerm(sender, label, false)) {
			Chatty.NoPermissions(sender);
			return true;
		}

		if (args.length < 1) {
			Chatty.SendError(sender, "You must specify an item.");
			return true;
		}

		ItemStack item = ItemDb.get(args[0], 0);
		if (item == null) {
			Chatty.WrongItem(sender, args[0]);
			return true;
		}

		Player player = (Player) sender;
		int total = 0;
		for (Offer o : DatabaseManager.GetSellerOffers(player.getName(), item)) {
			total += o.item.getAmount();
		}

		if (total == 0) {
			Chatty.SendError(sender, "You do not have any " + args[0]
					+ " for sale.");
			return true;
		}

		item.setAmount(total);
		new InventoryManager(player).addItem(item);
		DatabaseManager.RemoveSellerOffers(player, item);
		Chatty.SendSuccess(sender,
				"Removed " + Chatty.FormatAmount(Integer.valueOf(total)) + " "
						+ Chatty.FormatItem(args[0]));
		return true;
	}

}
