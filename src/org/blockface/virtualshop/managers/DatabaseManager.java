package org.blockface.virtualshop.managers;

import java.util.List;

import org.blockface.virtualshop.VirtualShop;
import org.blockface.virtualshop.objects.Offer;
import org.blockface.virtualshop.objects.Transaction;
import org.blockface.virtualshop.persistance.StockTable;
import org.blockface.virtualshop.persistance.TransactionsTable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DatabaseManager {

	public static List<Offer> GetItemOffers(ItemStack item) {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.eq("item", item.getTypeId())
				.eq("damage", item.getDurability())
				.eq("amount", item.getAmount()).findList();
		return Offer.ListOffers(stock);
	}

	public static void DeleteItem(int id) {
		StockTable stock = VirtualShop.es.find(StockTable.class).where()
				.eq("item", id).findUnique();
		VirtualShop.es.delete(stock);
	}

	public static void UpdateQuantity(int id, int left) {
		StockTable stock = VirtualShop.es.find(StockTable.class).where()
				.eq("item", id).findUnique();
		stock.setAmount(left);
		VirtualShop.es.save(stock);
	}

	public static void LogTransaction(Transaction t) {
		TransactionsTable tt = new TransactionsTable();
		tt.setSeller(t.seller);
		tt.setBuyer(t.buyer);
		tt.setItem(t.item.getTypeId());
		tt.setDamage(t.item.getDurability());
		tt.setAmount(t.item.getAmount());
		tt.setCost(t.cost);
		VirtualShop.es.save(tt);
	}

	public static void RemoveSellerOffers(Player player, ItemStack item) {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.ieq("seller", player.getName()).eq("item", item.getTypeId())
				.eq("damage", item.getDurability())
				.eq("amount", item.getAmount()).findList();
		VirtualShop.es.delete(stock);
	}

	public static List<Offer> GetPrices(ItemStack item) {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.eq("item", item.getTypeId())
				.eq("damage", item.getDurability())
				.eq("amount", item.getAmount()).findList();
		return Offer.ListOffers(stock);
	}

	public static List<Transaction> GetTransactions() {
		List<TransactionsTable> tt = VirtualShop.es.find(
				TransactionsTable.class).findList();
		return Transaction.ListTransactions(tt);
	}

	public static List<Transaction> GetTransactions(String action, String search) {
		List<TransactionsTable> tt = VirtualShop.es
				.find(TransactionsTable.class).where().ieq(action, search)
				.findList();
		return Transaction.ListTransactions(tt);
	}

	public static void AddOffer(Offer o) {
		StockTable stock = new StockTable();
		stock.setSeller(o.seller);
		stock.setItem(o.item.getTypeId());
		stock.setDamage(o.item.getDurability());
		stock.setAmount(o.item.getAmount());
		stock.setPrice(o.price);
		VirtualShop.es.save(stock);
	}

	public static List<Offer> GetBestPrices() {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.orderBy("price desc").findList();
		return Offer.ListOffers(stock);
	}

	public static List<Offer> SearchBySeller(String seller) {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.ieq("seller", seller).findList();
		return Offer.ListOffers(stock);
	}

	public static List<Offer> GetSellerOffers(String name, ItemStack item) {
		List<StockTable> stock = VirtualShop.es.find(StockTable.class).where()
				.ieq("seller", name).eq("item", item.getTypeId())
				.eq("damage", item.getDurability())
				.eq("amount", item.getAmount()).findList();
		return Offer.ListOffers(stock);
	}

	public static void AddOffer(String name, ItemStack item, float price) {
		StockTable stock = new StockTable();
		stock.setSeller(name);
		stock.setItem(item.getTypeId());
		stock.setDamage(item.getDurability());
		stock.setAmount(item.getAmount());
		stock.setPrice(price);
		VirtualShop.es.save(stock);
	}

}
