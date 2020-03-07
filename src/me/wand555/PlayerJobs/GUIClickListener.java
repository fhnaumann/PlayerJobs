package me.wand555.PlayerJobs;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIClickListener implements Listener {
	
	private PlayerJobs plugin;
	private GUIClasses gui;
	
	public GUIClickListener(JavaPlugin plugin, GUIClasses gui) {
		this.plugin = (PlayerJobs) plugin;
		this.gui = gui;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onGUIClickEvent(InventoryClickEvent event) {
		if(event.getClickedInventory() != null) {
			if(event.getCurrentItem() != null) {
				if(event.getWhoClicked() instanceof Player) {
					Player p = (Player) event.getWhoClicked();
					if(p.getOpenInventory().getTitle().equalsIgnoreCase("Jobs - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//click on job
						if(slot < 45) {
							UUID uuid = UUID.fromString(event.getCurrentItem().getItemMeta().getLore().get(0).substring(8).trim());
							Job job = Job.getJobFromUUID(uuid);
							if(job.getStatus() != JobStatus.IN_PROGRESS) {
								//if(!job.getCreator().equals(p.getUniqueId())) {
									job.setWorkingPlayer(p.getUniqueId());
									job.setStatus(JobStatus.IN_PROGRESS);
									this.reloadAllInvsOnChange();
									p.sendMessage("You accepted the job! Start working on it!");
								//}
								//else {
								//	p.sendMessage("You cannot work on your own job!");
								//}
							}
							
							
						}
						//previous page
						if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage("You're currently on page 1!");
							}
							else {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())-1);
								gui.createInventory(p, "jobs");
							}
						}
						//next page
						else if(slot == 53) {
							ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsAsList(PlayerJobs.filterStatus.get(p.getUniqueId()));
							if(GUIClasses.nextPageExists(p.getUniqueId(), jobs.size())) {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())+1);
								gui.createInventory(p, "jobs");
							}
							else {
								p.sendMessage("Only this page exists!");
							}
						}
						//filter jobs (later)
						else if(slot == 49) {
							switch(PlayerJobs.filterStatus.get(p.getUniqueId())) {
							case ALL:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.AWAITING);
								break;
							case AWAITING:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.IN_PROGRESS);
								break;
							case FINISHED:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								break;
							case IN_PROGRESS:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								break;
							default:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								break;
							}
							gui.createInventory(p, "jobs");
						}
						else if(slot == 47) {
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
							gui.createInventory(p, "jobs created");
						}
						else if(slot == 51) {
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
							gui.createInventory(p, "jobs accepted");
						}
					}
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase("Jobs Created - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//when job clicked
						if(slot < 45) {
							UUID uuid = UUID.fromString(event.getCurrentItem().getItemMeta().getLore().get(0).substring(8).trim());
							Job job = Job.getJobFromUUID(uuid);
							if(job.getStatus() == JobStatus.IN_PROGRESS) {
								job.setStatus(JobStatus.FINISHED);
								Job.removeJobFromUUID(uuid);
								gui.createInventory(p, "jobs created");
								OfflinePlayer worker = Bukkit.getOfflinePlayer(job.getWorkingPlayer());
								PlayerJobs.getEcon().depositPlayer(worker, job.getJobPayment());
								p.sendMessage("Successfully finished the job. The player received their money!");
							}
							else {
								Job.removeJobFromUUID(uuid);
								gui.createInventory(p, "jobs created");
								this.reloadAllInvsOnChange();
								Player player = Bukkit.getPlayer(job.getCreator());
								PlayerJobs.getEcon().depositPlayer(player, player.getWorld().getName(), job.getJobPayment());
								//this.updateInventoryOneTickLater();
								//ALLE AKTUELL GEÖFFNETEN INVS UPDATEN
								p.sendMessage("Successfully deleted the job!");
								p.sendMessage("The money has been deposited back to your balance!");
							}
							
						}
						//previous page
						else if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage("You're currently on page 1!");
							}
							else {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())-1);
								gui.createInventory(p, "jobs created");
							}
						}
						//go back
						else if(slot == 49) {
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
							gui.createInventory(p, "jobs");
						}
						//next page
						else if(slot == 53) {
							ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsFromSpecificPlayer(p.getUniqueId());
							if(GUIClasses.nextPageExists(p.getUniqueId(), jobs.size())) {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())+1);
								gui.createInventory(p, "jobs created");
							}
							else {
								p.sendMessage("Only this page exists!");
							}
						}
					}
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase("Jobs Accepted - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//job clicked
						if(slot < 45) {
							UUID uuid = UUID.fromString(event.getCurrentItem().getItemMeta().getLore().get(0).substring(8).trim());
							Job job = Job.getJobFromUUID(uuid);
							job.setStatus(JobStatus.AWAITING);
							gui.createInventory(p, "jobs accepted");
							this.reloadAllInvsOnChange();
							p.sendMessage("Successfully quit the job!");
						}					
						//previous page
						else if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage("You're currently on page 1!");
							}
							else {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())-1);
								gui.createInventory(p, "jobs accepted");
							}
						}
						//go back
						else if(slot == 49) {
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
							gui.createInventory(p, "jobs");
						}
						//next page
						else if(slot == 53) {
							ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsPlayerAccepted(p.getUniqueId());
							if(GUIClasses.nextPageExists(p.getUniqueId(), jobs.size())) {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())+1);
								gui.createInventory(p, "jobs accepted");
							}
							else {
								p.sendMessage("Only this page exists!");
							}
						}
					}
				}
			}	
		}
	}

	
	public void reloadAllInvsOnChange() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getOpenInventory().getTitle().contains("Jobs - Page:")) {
				gui.createInventory(p, "jobs");
			}
		}
	}
	
	public void updateInventoryOneTickLater() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.updateInventory();
				}
			}
		}.runTaskLater(plugin, 1L);	
	}
}
