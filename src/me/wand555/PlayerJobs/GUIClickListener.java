package me.wand555.PlayerJobs;

import java.util.ArrayList;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
					if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Jobs - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//click on job
						if(slot < 45) {
							if(event.getCurrentItem().getType() == Material.PAPER) {
								UUID uuid = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).substring(8).trim());
								Job job = Job.getJobFromUUID(uuid);
								if(job.getStatus() != JobStatus.IN_PROGRESS) {
									if(!job.getCreator().equals(p.getUniqueId())) {
										if(p.hasPermission("jobs.take")) {
											job.setWorkingPlayer(p.getUniqueId());
											job.setStatus(JobStatus.IN_PROGRESS);
											//GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(PlayerJobs.filterStatus.get(p.getUniqueId())));
											this.reloadAllInvsOnChange();
											p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You accepted the job! Start working on it!");
										}
										
									}
									else {
										p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You cannot work on your own job!");
									}
								}
							}		
						}
						//previous page
						if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You're currently on page " + ChatColor.GOLD + "1" + ChatColor.GRAY + "!");
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
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Only " + ChatColor.GOLD 
										+ PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) 
										+ ChatColor.GRAY + " page(s) exist!");
							}
						}
						//filter jobs (later)
						else if(slot == 49) {
							switch(PlayerJobs.filterStatus.get(p.getUniqueId())) {
							case ALL:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.AWAITING);
								GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(JobStatus.AWAITING));
								break;
							case AWAITING:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.IN_PROGRESS);
								GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(JobStatus.IN_PROGRESS));
								break;
							case FINISHED:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(JobStatus.ALL));
								break;
							case IN_PROGRESS:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(JobStatus.ALL));
								break;
							default:
								PlayerJobs.filterStatus.put(p.getUniqueId(), JobStatus.ALL);
								GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(JobStatus.ALL));
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
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Jobs Created - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//when job clicked
						if(slot < 45) {
							if(event.getCurrentItem().getType() == Material.PAPER) {
								UUID uuid = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).substring(8).trim());
								Job job = Job.getJobFromUUID(uuid);
								if(job.getStatus() == JobStatus.IN_PROGRESS) {
									if(p.hasPermission("jobs.finish")) {
										job.setStatus(JobStatus.FINISHED);
										OfflinePlayer worker = Bukkit.getOfflinePlayer(job.getWorkingPlayer());
										PlayerJobs.getEcon().depositPlayer(worker, job.getJobPayment());
										Job.removeJobFromUUID(uuid);
										gui.createInventory(p, "jobs created");
										this.reloadAllInvsOnChange();			
										p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully finished the job.");
										p.sendMessage(PlayerJobs.PREFIX + ChatColor.GOLD + Bukkit.getOfflinePlayer(job.getWorkingPlayer()) +
												ChatColor.GRAY + " received " + ChatColor.GOLD + 
												job.getJobPayment() + PlayerJobs.getEcon().currencyNamePlural() +
												ChatColor.GRAY + " to their balance!");
										if(Bukkit.getOfflinePlayer(job.getWorkingPlayer()).isOnline()) {
											((Player) Bukkit.getOfflinePlayer(job.getWorkingPlayer()))
												.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You received " +
														ChatColor.GOLD + job.getJobPayment() + PlayerJobs.getEcon().currencyNamePlural() +
														ChatColor.GRAY + "!");
										}
									}						
								}
								else {
									if(p.hasPermission("jobs.remove")) {
										Player player = Bukkit.getPlayer(job.getCreator());
										PlayerJobs.getEcon().depositPlayer(player, player.getWorld().getName(), job.getJobPayment());
										Job.removeJobFromUUID(uuid);
										gui.createInventory(p, "jobs created");
										this.reloadAllInvsOnChange();				
										p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully deleted the job!");
										p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "The money has been deposited back to your account!");
									}					
								}
							}			
						}
						//previous page
						else if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You're currently on page " + ChatColor.GOLD + "1" + ChatColor.GRAY + "!");
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
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Only " + ChatColor.GOLD 
										+ PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) 
										+ ChatColor.GRAY + " page(s) exist!");
							}
						}
					}
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Jobs Accepted - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//job clicked
						if(slot < 45) {
							if(event.getCurrentItem().getType() == Material.PAPER) {
								if(p.hasPermission("jobs.quit")) {
									UUID uuid = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).substring(8).trim());
									Job job = Job.getJobFromUUID(uuid);
									job.setStatus(JobStatus.AWAITING);
									job.setWorkingPlayer(null);
									gui.createInventory(p, "jobs accepted");
									this.reloadAllInvsOnChange();
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully quit the job!");
								}
							}						
						}					
						//previous page
						else if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You're currently on page " + ChatColor.GOLD + "1" + ChatColor.GRAY + "!");
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
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Only " + ChatColor.GOLD 
										+ PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) 
										+ ChatColor.GRAY + " page(s) exist!");
							}
						}
					}
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Override Jobs - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()))) {
						int slot = event.getRawSlot();
						if(slot <= 53) event.setCancelled(true);
						//job clicked
						if(slot < 45) {
							if(event.getCurrentItem().getType() == Material.PAPER) {
								UUID uuid = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).substring(8).trim());
								PlayerJobs.overrideJobsItems.put(p.getUniqueId(), uuid);
								gui.createInventory(p, "override decision");
							}			
						}					
						//previous page
						else if(slot == 45) {
							if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) == 1) {
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You're currently on page " + ChatColor.GOLD + "1" + ChatColor.GRAY + "!");
							}
							else {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())-1);
								gui.createInventory(p, "jobsoverride");
							}
						}
						//filter
						else if(slot == 49) {
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
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
							gui.createInventory(p, "jobsoverride");
						}
						//next page
						else if(slot == 53) {
							//nicht die von p, sondern die von args[0]
							ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsFromSpecificPlayer(PlayerJobs.overridejobs.get(p.getUniqueId()));
							if(GUIClasses.nextPageExists(p.getUniqueId(), jobs.size())) {
								PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())+1);
								gui.createInventory(p, "jobsoverride");
							}
							else {
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Only " + ChatColor.GOLD 
										+ PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) 
										+ ChatColor.GRAY + " page(s) exist!");
							}
						}
					}
					else if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Take Action")) {
						int slot = event.getRawSlot();
						if(slot <= 8) event.setCancelled(true);
						if(slot == 0) {
							//go back
							PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), 1);
							gui.createInventory(p, "jobsoverride");
						}
						else if(slot == 2) {
							//finish
							//Job job = Job.getJobFromUUID(PlayerJobs.overridejobs.get(p.getUniqueId()));
							if(p.hasPermission("jobs.override.finish")) {
								UUID itemUUID = PlayerJobs.overrideJobsItems.get(p.getUniqueId());
								Job job = Job.getJobFromUUID(itemUUID);
								if(job.getStatus() == JobStatus.IN_PROGRESS) {
									job.setStatus(JobStatus.FINISHED);
									OfflinePlayer worker = Bukkit.getOfflinePlayer(job.getWorkingPlayer());
									PlayerJobs.getEcon().depositPlayer(worker, job.getJobPayment());
									Job.removeJobFromUUID(itemUUID);
									//gui.createInventory(p, "jobsoverride");
									this.reloadAllInvsOnChange();
									gui.createInventory(p, "jobsoverride");
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully finished the job.");
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GOLD + job.getWorkingPlayer() +
											ChatColor.GRAY + " received " + ChatColor.GOLD + 
											job.getJobPayment() + PlayerJobs.getEcon().currencyNamePlural() +
											ChatColor.GRAY + " to their balance!");
								}
								else {
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Cannot finish a job that is not in progress!");
								}
							}			
						}
						else if(slot == 5) {
							//fire
							if(p.hasPermission("jobs.override.fire")) {
								UUID itemUUID = PlayerJobs.overrideJobsItems.get(p.getUniqueId());
								Job job = Job.getJobFromUUID(itemUUID);
								if(job.getStatus() == JobStatus.IN_PROGRESS) {
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Fired " + 
										ChatColor.GOLD + Bukkit.getOfflinePlayer(job.getWorkingPlayer()).getName() + 
										ChatColor.GRAY + " from the job!");
									job.setStatus(JobStatus.AWAITING);
									job.setWorkingPlayer(null);
									this.reloadAllInvsOnChange();																			
									gui.createInventory(p, "jobsoverride");	
								}
								else {
									p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "No one is working on this job!");
								}
							}					
						}
						else if(slot == 8) {
							//delete
							if(p.hasPermission("jobs.override.remove")) {
								UUID itemUUID = PlayerJobs.overrideJobsItems.get(p.getUniqueId());
								Job job = Job.getJobFromUUID(itemUUID);
								PlayerJobs.getEcon().depositPlayer(Bukkit.getOfflinePlayer(job.getCreator()), job.getJobPayment());
								Job.removeJobFromUUID(itemUUID);
								this.reloadAllInvsOnChange();
								gui.createInventory(p, "jobsoverride");
								p.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully deleted the job!");
								p.sendMessage(PlayerJobs.PREFIX + "The money has been deposited back to " + 
								ChatColor.GOLD + Bukkit.getOfflinePlayer(job.getCreator()).getName() + 
								ChatColor.GRAY + "'s account!");
						
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
				GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(PlayerJobs.filterStatus.get(p.getUniqueId())));
				gui.createInventory(p, "jobs");
			}
			else if(p.getOpenInventory().getTitle().contains("Jobs Created - Page")) {
				GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(PlayerJobs.filterStatus.get(p.getUniqueId())));
				gui.createInventory(p, "jobs created");
			}
			else if(p.getOpenInventory().getTitle().contains("Jobs Accepted - Page")) {
				GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(PlayerJobs.filterStatus.get(p.getUniqueId())));
				gui.createInventory(p, "jobs accepted");
			}
			else if(p.getOpenInventory().getTitle().contains("Override Jobs - Page")) {
				GUIClasses.reducePageIfNeccessary(p, Job.getJobsAsListSize(PlayerJobs.filterStatus.get(p.getUniqueId())));
				gui.createInventory(p, "jobsoverride");
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
