package org.blockface.virtualshop.objects;

import java.util.ArrayList;
import java.util.List;

import org.blockface.virtualshop.persistance.StockTable;
import org.bukkit.inventory.ItemStack;

public class Offer {
	public ItemStack item;
	public double price;
	public String seller;
	public int id;

	public Offer(String seller, int id, short damage, double price, int amount) {
		this.item = new ItemStack(id, amount, damage);
		this.seller = seller;
		this.price = price;
	}

	public Offer(String seller, ItemStack item, double price) {
		this.seller = seller;
		this.item = item;
		this.price = price;
	}

	public static List<Offer> ListOffers(List<StockTable> st) {
		List<Offer> ret = new ArrayList<Offer>();
		for (StockTable s1 : st) {
			Offer o = new Offer(s1.getSeller(), s1.getItem(),
					(short) s1.getDamage(), s1.getPrice(), s1.getAmount());
			o.id = s1.getId();
			ret.add(o);
		}
		return ret;
	}
}