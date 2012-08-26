package org.blockface.virtualshop.commands;

import java.util.Iterator;

import org.blockface.virtualshop.Chatty;
import org.blockface.virtualshop.VirtualShop;
import org.blockface.virtualshop.managers.ConfigManager;
import org.blockface.virtualshop.managers.DatabaseManager;
import org.blockface.virtualshop.objects.Offer;
import org.blockface.virtualshop.util.InventoryManager;
import org.blockface.virtualshop.util.ItemDb;
import org.blockface.virtualshop.util.Numbers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {
	private VirtualShop plugin;

	public SellCommand(VirtualShop instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (plugin.hasPerm(sender, label, false)) {
			Chatty.NoPermissions(sender);
			return true;
		}

		if (args.length < 3) {
			Chatty.SendError(sender,
					"Proper usage is /sell <amount> <item> <price>");
			return true;
		}

		float price = Numbers.ParseFloat(args[2]).floatValue();
		int amount = Numbers.ParseInteger(args[0]).intValue();
		if ((amount < 0) || (price < 0.0F)) {
			Chatty.NumberFormat(sender);
			return true;
		}

		Player player = (Player) sender;
		ItemStack item = ItemDb.get(args[1], amount);
		if (args[1].equalsIgnoreCase("hand")) {
			item = new ItemStack(player.getItemInHand().getType(), amount,
					player.getItemInHand().getDurability());
			args[1] = ItemDb.reverseLookup(item);
		}

		if (item == null) {
			Chatty.WrongItem(sender, args[1]);
			return true;
		}

		InventoryManager im = new InventoryManager(player);
		if (!im.contains(item, true, true)) {
			Chatty.SendError(
					sender,
					"You do not have "
							+ Chatty.FormatAmount(Integer.valueOf(item
									.getAmount())) + " "
							+ Chatty.FormatItem(args[1]));
			return true;
		}

		im.remove(item, true, true);
		int a = 0;
		Offer o;
		for (Iterator<Offer> i$ = DatabaseManager.GetSellerOffers(
				player.getName(), item).iterator(); i$.hasNext(); a += o.item
				.getAmount())
			o = (Offer) i$.next();
		DatabaseManager.RemoveSellerOffers(player, item);
		item.setAmount(item.getAmount() + a);
		o = new Offer(player.getName(), item, price);
		DatabaseManager.AddOffer(o);

		if (ConfigManager.BroadcastOffers().booleanValue()) {
			Chatty.BroadcastOffer(o);
			return true;
		}
		return false;
	}

}
