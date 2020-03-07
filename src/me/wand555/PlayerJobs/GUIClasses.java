package me.wand555.PlayerJobs;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GUIClasses {
	private PlayerJobs plugin;
	
	
	public GUIClasses(PlayerJobs playerJobs) {
		this.plugin = playerJobs;
	}
	
	public void createInventory(Player p, String msg) {
		Inventory gui = null;
		if(msg.equalsIgnoreCase("jobs")) {
			gui = plugin.getServer().createInventory(null, 54, "Jobs - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			ArrayList<Job> d = (ArrayList<Job>) Job.getJobsAsList(PlayerJobs.filterStatus.get(p.getUniqueId()));
			int page = PlayerJobs.pageCurrentlyOn.get(p.getUniqueId());
			int index = page * PlayerJobs.GUI_PAGE_SIZE - PlayerJobs.GUI_PAGE_SIZE;
			int endIndex = (index >= d.size()) ? d.size() - 1 : index + PlayerJobs.GUI_PAGE_SIZE;
			for(; index < endIndex; index++) {
				if(index < d.size()) {
					Job job = d.get(index);
					gui.setItem(index%PlayerJobs.GUI_PAGE_SIZE, createJobDisplayItem(
							job.getUuid(), 
							job.getCreator(), 
							job.getJobName(), 
							job.getJobPayment(), 
							job.getJobDescription(), 
							job.getStatus(), 
							job.getWorkingPlayer(),
							"public"));
				}
				
			}
			for(int i=0; i<gui.getSize(); i++) {
				if(i == 45) {
					//place netherstar
					gui.setItem(i, createPageItem(false));
				}
				else if(i == 47) {
					//place jobs player created
					gui.setItem(i, createJobsCreatedItem());
				}
				else if(i == 49) {
					//place filter
					gui.setItem(i, createFilterItem(PlayerJobs.filterStatus.get(p.getUniqueId())));
				}
				else if(i == 51) {
					//place jobs player completed
					gui.setItem(i, createJobsAcceptedItem());
				}
				else if(i == 53) {
					//place netherstar
					gui.setItem(i, createPageItem(true));
				}
				else if(i >= 46 && i<= 52) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("jobs created")) {
			gui = plugin.getServer().createInventory(null, 54, "Jobs Created - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsFromSpecificPlayer(p.getUniqueId());
			int page = PlayerJobs.pageCurrentlyOn.get(p.getUniqueId());
			int index = page * PlayerJobs.GUI_PAGE_SIZE - PlayerJobs.GUI_PAGE_SIZE;
			int endIndex = (index >= jobs.size()) ? jobs.size() - 1 : index + PlayerJobs.GUI_PAGE_SIZE;
			for(; index < endIndex; index++) {
				if(index < jobs.size()) {
					Job job = jobs.get(index);
					gui.setItem(index%PlayerJobs.GUI_PAGE_SIZE, createJobDisplayItem(
							job.getUuid(), 
							job.getCreator(), 
							job.getJobName(), 
							job.getJobPayment(), 
							job.getJobDescription(), 
							job.getStatus(), 
							job.getWorkingPlayer(),
							"personal"));
				}
			}
			
			for(int i=0; i<gui.getSize(); i++) {
				if(i == 45) {
					//place netherstar
					gui.setItem(i, createPageItem(false));
				}
				else if(i == 49) {
					//place go back
					gui.setItem(i, createGoBack());
				}
				else if(i == 53) {
					//place netherstar
					gui.setItem(i, createPageItem(true));
				}
				else if(i >= 46 && i<= 52) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("jobs accepted")) {
			gui = plugin.getServer().createInventory(null, 54, "Jobs Accepted - Page: " + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			ArrayList<Job> jobs = (ArrayList<Job>) Job.getJobsPlayerAccepted(p.getUniqueId());
			int page = PlayerJobs.pageCurrentlyOn.get(p.getUniqueId());
			int index = page * PlayerJobs.GUI_PAGE_SIZE - PlayerJobs.GUI_PAGE_SIZE;
			int endIndex = (index >= jobs.size()) ? jobs.size() - 1 : index + PlayerJobs.GUI_PAGE_SIZE;
			for(; index < endIndex; index++) {
				if(index < jobs.size()) {
					Job job = jobs.get(index);
					gui.setItem(index%PlayerJobs.GUI_PAGE_SIZE, createJobDisplayItem(
							job.getUuid(), 
							job.getCreator(), 
							job.getJobName(), 
							job.getJobPayment(), 
							job.getJobDescription(), 
							job.getStatus(), 
							job.getWorkingPlayer(),
							"accepted"));
				}
			}
			
			for(int i=0; i<gui.getSize(); i++) {
				if(i == 45) {
					//place netherstar
					gui.setItem(i, createPageItem(false));
				}
				else if(i == 49) {
					//place go back
					gui.setItem(i, createGoBack());
				}
				else if(i == 53) {
					//place netherstar
					gui.setItem(i, createPageItem(true));
				}
				else if(i >= 46 && i<= 52) {
					gui.setItem(i, createGlass());
				}
			}
		}
		
		if(gui != null) p.openInventory(gui);
	}
	
	private ItemStack createGlass() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * @param uuid truly unique ID
	 * @param creator player who created job
	 * @param jobName name of the job
	 * @param payment money paid after completion
	 * @param description description of the job
	 * @param status current status
	 * @param workingPlayer when status == IN_PROGRESS the player working will be displayed
	 * @param type for lore customization on display
	 * 
	 * @return ItemStack ready for display
	 */
	private ItemStack createJobDisplayItem(UUID uuid, UUID creator, String jobName, int payment, String description, JobStatus status, UUID workingPlayer, String type) {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(jobName);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Job ID: " + uuid.toString());
		lore.add("Creator: " + Bukkit.getOfflinePlayer(creator).getName());
		lore.add("Payment: " + payment);
		lore.add("Description: " + description);
		lore.add("Status: " + status.toString());
		lore.add("Working player: " + ((workingPlayer != null) ? Bukkit.getOfflinePlayer(workingPlayer).getName() : "-"));
		if(type.equalsIgnoreCase("personal")) {
			lore.add((status == JobStatus.IN_PROGRESS) ? "Click to finish the job!"
					: "Click to delete job!");
		}
		else if(type.equalsIgnoreCase("accepted")) {
			lore.add("Click if you wish to quit this job!");
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createPageItem(boolean right) {
		ItemStack item = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((right) ? "Click to switch to next page!" : "Click to switch to previous page!");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createFilterItem(JobStatus currentStatus) {
		ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Filter jobs");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Currently filtering:");
		switch(currentStatus) {
		case ALL:
			lore.add("All jobs");
			break;
		case AWAITING:
			lore.add("Only jobs available");
			break;
		case FINISHED:
			lore.add("Completed jobs");
			break;
		case IN_PROGRESS:
			lore.add("Jobs in progress");
			break;
		default:
			lore.add("None");
			break;
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createJobsCreatedItem() {
		ItemStack item = new ItemStack(Material.GREEN_SHULKER_BOX);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Jobs you created");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createJobsAcceptedItem() {
		ItemStack item = new ItemStack(Material.BLUE_SHULKER_BOX);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Jobs you accepted");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createGoBack() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Click to go back!");
		item.setItemMeta(meta);
		return item;
	}
	
	public static boolean nextPageExists(UUID uuid, int size) {
		if(PlayerJobs.pageCurrentlyOn.containsKey(uuid)) {
			return PlayerJobs.pageCurrentlyOn.get(uuid) * PlayerJobs.GUI_PAGE_SIZE < size;
		}
		return false;
	}

}
