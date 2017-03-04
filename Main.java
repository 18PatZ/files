package com.patrickzhong.blockpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class Main extends JavaPlugin implements Listener {
	
	static Config conf;
	static Config data;
	
	public void onEnable(){
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		HashMap<String, Object> defs = new HashMap<String, Object>();
		defs.put("GUI.Rows", 1);
		
		conf = new Config(this, defs, "config");
		
		data = new Config(this, null, "data");
	
		
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(this, "today", 
					new PlaceholderReplacer(){
						public String onPlaceholderReplace(PlaceholderReplaceEvent ev){
							return (int)PointsManager.getDailyPoints(ev.getPlayer())+"";
						}
					}
			);
			
			PlaceholderAPI.registerPlaceholder(this, "week", 
					new PlaceholderReplacer(){
						public String onPlaceholderReplace(PlaceholderReplaceEvent ev){
							return (int)PointsManager.getWeeklyPoints(ev.getPlayer())+"";
						}
					}
			);
			
			PlaceholderAPI.registerPlaceholder(this, "total", 
					new PlaceholderReplacer(){
						public String onPlaceholderReplace(PlaceholderReplaceEvent ev){
							return (int)PointsManager.getTotalPoints(ev.getPlayer())+"";
						}
					}
			);

			PlaceholderAPI.registerPlaceholder(this, "current", 
					new PlaceholderReplacer(){
						public String onPlaceholderReplace(PlaceholderReplaceEvent ev){
							return (int)PointsManager.getCurrentPoints(ev.getPlayer())+"";
						}
					}
			);

			PlaceholderAPI.registerPlaceholder(this, "challenge", 
					new PlaceholderReplacer(){
						public String onPlaceholderReplace(PlaceholderReplaceEvent ev){
							int ch = (int)getChallenge();
							return ch == Integer.MAX_VALUE ? "None" : (ch+"");
						}
					}
			);
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("bp") || cmd.getName().equalsIgnoreCase("points")){
			
			if(sender instanceof Player)
				GUIManager.openGUI((Player) sender); 
			else
				sender.sendMessage(tacc("&cYou must be a player."));
			
			return true;
		}
		
		if(!sender.hasPermission("blockpoints.dailymine")){
			sender.sendMessage(tacc("&4You do not have the permission &cblockpoints.dailymine&4."));
			return true;
		}
		
		if(args.length == 0){
			if(sender instanceof Player){
				conf.load().config.set("Daily Challenge.Reward", ((Player) sender).getItemInHand());
				conf.save();
				sender.sendMessage(tacc("&7Set the daily mine reward to the item in your hand!"));
			}
			else
				sender.sendMessage(tacc("&cYou must be a player."));
		}
		else {
			try {
				int i = Integer.parseInt(args[0]);
				conf.load().config.set("Daily Challenge.Amount", i);
				conf.save();
				sender.sendMessage(tacc("&7Set the daily mine amount to &e"+i+"&7!"));
			}
			catch (NumberFormatException e){
				sender.sendMessage(tacc("&c"+args[0]+" &4is not a valid number."));
			}
		}
		
		return true;
	}
	
	public static double getChallenge(){
		return conf.load().config.getDouble("Daily Challenge.Amount", Integer.MAX_VALUE);
	}
	
	public static ItemStack getReward(){
		return conf.config.getItemStack("Daily Challenge.Reward", new ItemStack(Material.AIR));
	}
	
	public double getMultiplier(Player player){
		if(!conf.load().config.contains("Multipliers"))
			return 1;
		
		double max = 1;
		for(String name : conf.config.getConfigurationSection("Multipliers").getKeys(false)){
			if(player.hasPermission("blockpoints.multiplier."+name)){
				double curr = conf.config.getDouble("Multipliers."+name);
				if(curr > max)
					max = curr;
			}
		}
		
		return max;
	}
	
	public double getPoints(Block block){
		return conf.load().config.getDouble("Blocks."+block.getType().toString().toUpperCase(), 1);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent ev){
		if(!ev.isCancelled()){
			PointsManager.addPoints(ev.getPlayer(), getPoints(ev.getBlock()) * getMultiplier(ev.getPlayer()));
			//ev.getPlayer().sendMessage(tacc("&7Block points: &e")+(int)PointsManager.getCurrentPoints(ev.getPlayer())+" "+(int)PointsManager.getDailyPoints(ev.getPlayer())+" "+(int)PointsManager.getWeeklyPoints(ev.getPlayer())+" "+(int)PointsManager.getTotalPoints(ev.getPlayer()));
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent ev){
		GUIManager.onClick(ev);
	}
	
	static String tacc(String str){
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	static ItemStack c(Material mat, int data, boolean enchanted, String disp, String... lore){
		ItemStack i = new ItemStack(mat, 1, (byte)data);
		if(enchanted)
			i.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemMeta im = i.getItemMeta();
		for(ItemFlag flag : ItemFlag.values())
			im.addItemFlags(flag);
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', disp));
		List<String> l = new ArrayList<String>();
		for(String str : lore)
			l.add(ChatColor.translateAlternateColorCodes('&', str));
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
}
