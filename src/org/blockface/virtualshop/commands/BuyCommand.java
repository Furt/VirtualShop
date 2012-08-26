package org.blockface.virtualshop.commands;

import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.blockface.virtualshop.Chatty;
import org.blockface.virtualshop.VirtualShop;
import org.blockface.virtualshop.managers.DatabaseManager;
import org.blockface.virtualshop.objects.Offer;
import org.blockface.virtualshop.objects.Transaction;
import org.blockface.virtualshop.util.InventoryManager;
import org.blockface.virtualshop.util.ItemDb;
import org.blockface.virtualshop.util.Numbers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyCommand implements CommandExecutor {
	private VirtualShop plugin;

	public BuyCommand(VirtualShop instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!plugin.hasPerm(sender, label, false)) {
			Chatty.NoPermissions(sender);
			return true;
		}

		if (args.length < 2) {
			Chatty.SendError(sender, "Proper usage is /buy <amount> <item>");
			return true;
		}

		int amount = Numbers.ParseInteger(args[0]).intValue();
		if (amount < 0) {
			Chatty.NumberFormat(sender);
			return true;
		}

		float maxprice = 1.0E+09F;
		if (args.length > 2) {
			maxprice = Numbers.ParseFloat(args[2]).floatValue();
			if (maxprice < 0.0F) {
				Chatty.NumberFormat(sender);
				return true;
			}
		}

		ItemStack item = ItemDb.get(args[1], 0);
		if (item == null) {
			Chatty.WrongItem(sender, args[1]);
			return true;
		}
		Player player = (Player) sender;
		Economy account = VirtualShop.economy;
		int bought = 0;
		double spent = 0.0D;
		InventoryManager im = new InventoryManager(player);
		List<Offer> offers = DatabaseManager.GetItemOffers(item);
		if (offers.size() == 0) {
			Chatty.SendError(sender,
					"There is no " + Chatty.FormatItem(args[1]) + " for sale.");
			return true;
		}
		for (Offer o : offers) {
			if (o.price <= maxprice) {
				if (o.seller.equals(player.getName()))
					return true;
				if (amount - bought >= o.item.getAmount()) {
					int canbuy = o.item.getAmount();
					double cost = o.price * canbuy;

					if (!account.has(player.getName(), cost)) {
						canbuy = (int) (account.getBalance(player.getName()) / o.price);
						cost = canbuy * o.price;
						if (canbuy < 1) {
							Chatty.SendError(player, "Ran out of money!");
							break;
						}
					}
					bought += canbuy;
					spent += cost;
					account.withdrawPlayer(player.getName(), cost);
					account.depositPlayer(o.seller, cost);
					Chatty.SendSuccess(
							o.seller,
							Chatty.FormatSeller(player.getName())
									+ " just bought "
									+ Chatty.FormatAmount(Integer
											.valueOf(canbuy)) + " "
									+ Chatty.FormatItem(args[1]) + " for "
									+ Chatty.FormatPrice(cost));
					int left = o.item.getAmount() - canbuy;
					if (left < 1)
						DatabaseManager.DeleteItem(o.id);
					else
						DatabaseManager.UpdateQuantity(o.id, left);
					Transaction t = new Transaction(o.seller, player.getName(),
							o.item.getTypeId(), o.item.getDurability(), canbuy,
							cost);
					DatabaseManager.LogTransaction(t);
				} else {
					int canbuy = amount - bought;
					double cost = canbuy * o.price;

					if (!account.has(player.getName(), cost)) {
						canbuy = (int) (account.getBalance(player.getName()) / o.price);
						cost = canbuy * o.price;
						if (canbuy < 1) {
							Chatty.SendError(player, "Ran out of money!");
							break;
						}
					}
					bought += canbuy;
					spent += cost;
					account.withdrawPlayer(player.getName(), cost);
					account.depositPlayer(o.seller, cost);
					Chatty.SendSuccess(
							o.seller,
							Chatty.FormatSeller(player.getName())
									+ " just bought "
									+ Chatty.FormatAmount(Integer
											.valueOf(canbuy)) + " "
									+ Chatty.FormatItem(args[1]) + " for "
									+ Chatty.FormatPrice(cost));
					int left = o.item.getAmount() - canbuy;
					DatabaseManager.UpdateQuantity(o.id, left);
					Transaction t = new Transaction(o.seller, player.getName(),
							o.item.getTypeId(), o.item.getDurability(), canbuy,
							cost);
					DatabaseManager.LogTransaction(t);
				}
				if (bought >= amount)
					break;
			}
		}
		item.setAmount(bought);
		if (bought > 0)
			im.addItem(item);
		Chatty.SendSuccess(
				player,
				"Managed to buy "
						+ Chatty.FormatAmount(Integer.valueOf(bought)) + " "
						+ Chatty.FormatItem(args[1]) + " for "
						+ Chatty.FormatPrice(spent));
		return true;
	}
}
