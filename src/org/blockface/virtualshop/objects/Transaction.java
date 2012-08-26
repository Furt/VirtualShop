package org.blockface.virtualshop.objects;

import java.util.ArrayList;
import java.util.List;

import org.blockface.virtualshop.persistance.TransactionsTable;
import org.bukkit.inventory.ItemStack;

public class Transaction {
	public String seller;
	public String buyer;
	public ItemStack item;
	public double cost;

	public Transaction(String seller, String buyer, int id, int damage,
			int amount, double cost) {
		this.seller = seller;
		this.buyer = buyer;
		this.item = new ItemStack(id, amount, (short) damage);
		this.cost = cost;
	}

	public static List<Transaction> ListTransactions(List<TransactionsTable> tt) {
		List<Transaction> ret = new ArrayList<Transaction>();
		for (TransactionsTable t1 : tt) {
			Transaction t = new Transaction(t1.getSeller(), t1.getBuyer(),
					t1.getItem(), t1.getDamage(), t1.getAmount(), t1.getCost());
			ret.add(t);
		}
		return ret;
	}
}