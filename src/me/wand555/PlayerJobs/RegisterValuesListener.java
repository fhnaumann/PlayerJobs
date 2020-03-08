package me.wand555.PlayerJobs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterValuesListener implements Listener {
	
	public RegisterValuesListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/*
	@EventHandler
	public void onGUICloseEvent(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();
			String title = p.getOpenInventory().getTitle();
			int page = PlayerJobs.pageCurrentlyOn.getOrDefault(p.getUniqueId(), 1);
			if(title.equalsIgnoreCase(ChatColor.GOLD + "Jobs - Page: " + ChatColor.GRAY + page)
					|| title.equalsIgnoreCase(ChatColor.GOLD + "Jobs Created - Page: " + ChatColor.GRAY + page)
					|| title.equalsIgnoreCase(ChatColor.GOLD + "Jobs Accepted - Page: " + ChatColor.GRAY + page)
					|| title.equalsIgnoreCase(ChatColor.GOLD + "Override Jobs - Page: " + ChatColor.GRAY + page)
					|| title.equalsIgnoreCase(ChatColor.GOLD + "Take Action")) {
				PlayerJobs.filterStatus.remove(p.getUniqueId());
				PlayerJobs.overridejobs.remove(p.getUniqueId());
				PlayerJobs.overrideJobsItems.remove(p.getUniqueId());
				PlayerJobs.pageCurrentlyOn.remove(p.getUniqueId());
			}
		}
		
	}*/
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		PlayerJobs.filterStatus.remove(p.getUniqueId());
		PlayerJobs.overridejobs.remove(p.getUniqueId());
		PlayerJobs.overrideJobsItems.remove(p.getUniqueId());
		PlayerJobs.pageCurrentlyOn.remove(p.getUniqueId());
	}
}
