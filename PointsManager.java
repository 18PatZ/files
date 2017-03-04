package com.patrickzhong.blockpoints;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PointsManager {
	
	public static void addPoints(Player player, double points){ addPoints(player.getName(), points); }
	
	public static boolean removePoints(Player player, double points){ return removePoints(player.getName(), points); }
	
	public static double getDailyPoints(Player player){ return getDailyPoints(player.getName()); }
	public static double getWeeklyPoints(Player player){ return getWeeklyPoints(player.getName()); }
	public static double getTotalPoints(Player player){ return getTotalPoints(player.getName()); }
	public static double getCurrentPoints(Player player){ return getCurrentPoints(player.getName()); }
	
	public static void addPoints(String player, double points){
		Main.data.load();
		long day = CalendarUtil.getDay();
		
		if(Main.data.config.contains("Players."+player) && !Main.data.config.contains("Players."+player+"."+day)){
			Main.data.config.set("Players."+player+".Daily Challenge", null);
			for(String key : Main.data.config.getConfigurationSection("Players."+player).getKeys(false))
				if(!key.contains("Total") && !key.contains("Daily") && !CalendarUtil.sameWeek(Long.parseLong(key), day))
					Main.data.config.set("Players."+player+"."+key, null);
		}
			
		if(points > 0){
			Main.data.config.set("Players."+player+"."+day, Main.data.config.getDouble("Players."+player+"."+day, 0) + points);
			Main.data.config.set("Players."+player+".Total", Main.data.config.getDouble("Players."+player+".Total", 0) + points);
		}
		
		Main.data.config.set("Players."+player+".Current Total", Main.data.config.getDouble("Players."+player+".Current Total", 0) + points);
		Main.data.save();
		
		double ch = Main.getChallenge();
		if(!hasCompletedChallenge(player) && getDailyPoints(player) >= ch){
			Main.data.config.set("Players."+player+".Daily Challenge", true);
			Main.data.save();
			Bukkit.getPlayer(player).sendMessage(Main.tacc("&7You completed the daily challenge to mine &e"+(int)ch+" &7block points!"));
			Bukkit.getPlayer(player).getInventory().addItem(Main.getReward());
			Bukkit.getPlayer(player).updateInventory();
		}
		
		
		
	}
	
	public static boolean hasCompletedChallenge(String player){
		Boolean bool = Main.data.load().config.getBoolean("Players."+player+".Daily Challenge");
		return bool == null ? false : bool.booleanValue();
	}
	
	public static boolean removePoints(String player, double points){
		if(getCurrentPoints(player) < points)
			return false;
		
		addPoints(player, -points);
		
		return true;
	}
	
	public static double getDailyPoints(String player){
		long day = CalendarUtil.getDay();
		return Main.data.load().config.getDouble("Players."+player+"."+day, 0);
	}
	
	public static double getWeeklyPoints(String player){
		Main.data.load();
		long day = CalendarUtil.getDay();
		
		double total = 0;
		
		if(Main.data.config.contains("Players."+player))
			for(String key : Main.data.config.getConfigurationSection("Players."+player).getKeys(false))
				if(!key.contains("Total") && !key.contains("Daily") && CalendarUtil.sameWeek(Long.parseLong(key), day))
					total += Main.data.config.getDouble("Players."+player+"."+key);
		
		return total;
	}
	
	public static double getTotalPoints(String player){
		return Main.data.load().config.getDouble("Players."+player+".Total", 0);
	}
	
	public static double getCurrentPoints(String player){
		return Main.data.load().config.getDouble("Players."+player+".Current Total", 0);
	}

}
