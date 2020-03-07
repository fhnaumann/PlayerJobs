package me.wand555.PlayerJobs;

import java.security.acl.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class PlayerJobs extends JavaPlugin {

	private PlayerJobs plugin;
	private CE myCE;
	private GUIClasses gui;
	public static HashMap<UUID, Integer> pageCurrentlyOn = new HashMap<>();
	public static HashMap<UUID, JobStatus> filterStatus = new HashMap<>();
	public static final int GUI_PAGE_SIZE = 2;
	
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		plugin = this;
		gui = new GUIClasses(this);	
		myCE = new CE(plugin, gui);
		new GUIClickListener(this, gui);
		System.out.println("!!!!!!!!!" + myCE.toString());
		this.getCommand("jobs").setExecutor(myCE);	
		if(!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //setupPermissions();
        setupChat();
	}
	
	public void onDisable() {
		
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
        System.out.println("perms: " + rsp);
        perms = rsp.getProvider();
        return perms != null;
    }

	public static Economy getEcon() {
		return econ;
	}
}
