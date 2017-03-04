package com.patrickzhong.blockpoints;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIManager {
	
	public static void openGUI(Player player){
		Inventory inv = Bukkit.createInventory(null, 9 * Main.conf.load().config.getInt("GUI.Rows"), "BlockPoints Shop");
		for(String slot : Main.conf.config.getConfigurationSection("GUI.Slots").getKeys(false))
			inv.setItem(Integer.parseInt(slot), Main.conf.config.getItemStack("GUI.Slots."+slot+".Item"));
		
		int daily = (int)PointsManager.getDailyPoints(player);
		int week = (int)PointsManager.getWeeklyPoints(player);
		int all = (int)PointsManager.getTotalPoints(player);
		int curr = (int)PointsManager.getCurrentPoints(player);
		
		int ch = (int)Main.getChallenge();
		
		ItemStack book;
		
		if(ch == Integer.MAX_VALUE){
			book = Main.c(Material.BOOK, 0, false, "&eBlock Points", 
					"&7Current Points: &e"+curr,
					"&7Total Mined Today: &e"+daily, 
					"&7Total Mined This Week: &e"+week,
					"&7Total Mined: &e"+all);
		}
		else if(daily >= ch){
			book = Main.c(Material.BOOK, 0, false, "&eBlock Points", 
					"&7Current Points: &e"+curr,
					"&7Total Mined Today: &e"+daily, 
					"&7Total Mined This Week: &e"+week,
					"&7Total Mined: &e"+all,
					"&7Challenge completed! Come back tomorrow!");
		}
		else {
			book = Main.c(Material.BOOK, 0, false, "&eBlock Points", 
					"&7Current Points: &e"+curr,
					"&7Total Mined Today: &e"+daily, 
					"&7Total Mined This Week: &e"+week,
					"&7Total Mined: &e"+all,
					"&7Daily Challenge: &e"+daily+"&7/&e"+ch);
		}
		
		
		inv.setItem(0, book);
		inv.setItem(8, book);
		
		
		player.openInventory(inv);
	}
	
	public static void onClick(InventoryClickEvent ev){
		if(ev.getInventory().getTitle().equals("BlockPoints Shop")){
			ev.setCancelled(true);
			if(ev.getRawSlot() >= ev.getInventory().getSize() || ev.getCurrentItem() == null
					|| ev.getCurrentItem().getType() == Material.AIR || ev.getCurrentItem().getType() == Material.BOOK)
				return;
			int slot = ev.getSlot();
			Player player = (Player) ev.getWhoClicked();
			double cost = Main.conf.load().config.getDouble("GUI.Slots."+slot+".Cost");
			if(PointsManager.removePoints(player, cost)){
				player.closeInventory();
				String cmd = Main.conf.config.getString("GUI.Slots."+slot+".Command");
				cmd = Main.tacc(cmd.replace("{player}", player.getName()));
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
				player.sendMessage(Main.tacc("&7You bought &e"+disp(ev.getCurrentItem())+" &7for &e"+(int)cost+" &7block points."));
			}
			else
				player.sendMessage(Main.tacc("&4You do not have &c"+(int)cost+" &4block points."));
		}
	}
	
	private static String disp(ItemStack i){
		try {
			String s = i.getItemMeta().getDisplayName();
			return s == null ? "" : s;
		}
		catch (NullPointerException e){
			return "";
		}
	}

}
