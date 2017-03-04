package com.patrickzhong.blockpoints;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Config {
	
	File configFile;
	public YamlConfiguration config;

	public Config(Plugin plugin, HashMap<String, Object> defaults, String name){
		
		if(!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdir();
		configFile = new File(plugin.getDataFolder(), name+".yml");
		boolean existed = true;
		if(!configFile.exists()){
			existed = false;
			try {
				configFile.createNewFile();
			} catch (IOException e) {
			}
		}
		load();
		if(defaults != null){
			for(String path : defaults.keySet())
				config.addDefault(path, defaults.get(path));
			config.options().copyDefaults(true);
			save();
		}
		if(!existed && name.equals("config")){
			load();
			
			if(!config.contains("Multipliers"))
				config.set("Multipliers.donor", 2);
			
			if(!config.contains("Blocks"))
				config.set("Blocks.STONE", 2);
			
			if(!config.contains("Daily Challenge")){
				config.set("Daily Challenge.Amount", 32);
				config.set("Daily Challenge.Reward", new ItemStack(Material.GOLD_INGOT, 16));
			}
			
			if(!config.contains("GUI.Items")){
				config.set("GUI.Slots.4.Item", Main.c(Material.DIAMOND_SWORD, 0, true, "&cWeapons and Tools", "&7Click to buy for &e500 &7points"));
				config.set("GUI.Slots.4.Cost", 500);
				config.set("GUI.Slots.4.Command", "kit Tools {player}");
			}
			save();
		}
	}
	
	public Config save(){
		try {
			config.save(configFile);
		}
		catch (Exception e){
		}
		
		return this;
	}
	
	public Config load(){
		config = YamlConfiguration.loadConfiguration(configFile);
		return this;
	}
	
}
