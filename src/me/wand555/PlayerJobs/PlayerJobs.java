package me.wand555.PlayerJobs;

import java.io.File;
import java.io.IOException;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class PlayerJobs extends JavaPlugin {

	private PlayerJobs plugin;
	private CE myCE;
	private GUIClasses gui;
	public static HashMap<UUID, Integer> pageCurrentlyOn = new HashMap<>();
	public static HashMap<UUID, JobStatus> filterStatus = new HashMap<>();
	//player and target player
	public static HashMap<UUID, UUID> overridejobs = new HashMap<>();
	//player and clicked item
	public static HashMap<UUID, UUID> overrideJobsItems = new HashMap<>();
	public static final int GUI_PAGE_SIZE = 45;
	public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerJobs" + ChatColor.GRAY + "]: ";
	
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		plugin = this;
		loadFromConfig();
		gui = new GUIClasses(this);	
		myCE = new CE(plugin, gui);
		new GUIClickListener(this, gui);
		new RegisterValuesListener(this);
		this.getCommand("jobs").setExecutor(myCE);	
		this.getCommand("jobscreate").setExecutor(myCE);
		this.getCommand("jobsoverride").setExecutor(myCE);
		if(!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //setupPermissions();
        //setupChat();
	}
	
	public void onDisable() {
		for(Player p : Bukkit.getOnlinePlayers()) p.closeInventory();
		storeToConfig();
	}
	
	private void storeToConfig() {
		File file = new File(plugin.getDataFolder()+"", "jobdata.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		for(Entry<UUID, Job> entry : Job.getJobs().entrySet()) {
			cfg.set(entry.getKey().toString()+".Creator", entry.getValue().getCreator().toString());
			cfg.set(entry.getKey().toString()+".Name", entry.getValue().getJobName());
			cfg.set(entry.getKey().toString()+".Payment", entry.getValue().getJobPayment());
			cfg.set(entry.getKey().toString()+".Description", entry.getValue().getJobDescriptionAsSingleString());
			cfg.set(entry.getKey().toString()+".Status", entry.getValue().getStatus().toString());
			cfg.set(entry.getKey().toString()+".WorkingPlayer", entry.getValue().getWorkingPlayer() == null ? "null" : entry.getValue().getWorkingPlayer().toString());
		}
		filterStatus.clear();
		overridejobs.clear();
		overrideJobsItems.clear();
		pageCurrentlyOn.clear();
		Job.getJobs().clear();
		saveCustomYml(cfg, file);
	}
	
	private void loadFromConfig() {
		this.checkOrdner();
		File file = new File(plugin.getDataFolder()+"", "jobdata.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		for(String s : cfg.getKeys(false)) {
			Job.insertNewJob(new Job(UUID.fromString(s.trim()), 
					UUID.fromString(cfg.getString(s + ".Creator").trim()), 
					cfg.getString(s + ".Name"), 
					cfg.getInt(s + ".Payment"), 
					cfg.getString(s + ".Description"),
					JobStatus.valueOf(cfg.getString(s + ".Status")),
					cfg.getString(s + ".WorkingPlayer").equalsIgnoreCase("null") ? null : UUID.fromString(cfg.getString(s + ".WorkingPlayer"))));
		}
	}
	
   public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
	   try {
		   ymlConfig.save(ymlFile);
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }
   
   public void checkOrdner() {
	   File file = new File(this.getDataFolder()+"");
	   if(!file.exists()) {
		   file.mkdir();
	   }
   }
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        
        chat = rsp.getProvider();
        return chat != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

	public static Economy getEcon() {
		return econ;
	}
}
