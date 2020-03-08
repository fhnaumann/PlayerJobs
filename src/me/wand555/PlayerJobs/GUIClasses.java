package me.wand555.PlayerJobs;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIClasses {
	private PlayerJobs plugin;
	
	
	public GUIClasses(PlayerJobs playerJobs) {
		this.plugin = playerJobs;
	}
	
	public void createInventory(Player p, String msg) {
		Inventory gui = null;
		if(msg.equalsIgnoreCase("jobs")) {
			gui = plugin.getServer().createInventory(null, 54, ChatColor.GOLD + "Jobs - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			this.placeListings(gui, p.getUniqueId(), (ArrayList<Job>) Job.getJobsAsList(PlayerJobs.filterStatus.get(p.getUniqueId())), "");
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
				else if(gui.getItem(i) == null) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("jobs created")) {
			gui = plugin.getServer().createInventory(null, 54, ChatColor.GOLD + "Jobs Created - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			this.placeListings(gui, p.getUniqueId(), (ArrayList<Job>) Job.getJobsFromSpecificPlayer(p.getUniqueId()), "created");			
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
				else if(gui.getItem(i) == null) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("jobs accepted")) {
			gui = plugin.getServer().createInventory(null, 54, ChatColor.GOLD + "Jobs Accepted - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			this.placeListings(gui, p.getUniqueId(), (ArrayList<Job>) Job.getJobsPlayerAccepted(p.getUniqueId()), "accepted");		
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
				else if(gui.getItem(i) == null) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("jobsoverride")) {
			gui = plugin.getServer().createInventory(null, 54, ChatColor.GOLD + "Override Jobs - Page: " + ChatColor.GRAY + PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()));
			
			this.placeListings(gui, 
					p.getUniqueId(), 
					(ArrayList<Job>) Job.getJobAsListFromSpecificPlayer(PlayerJobs.overridejobs.get(p.getUniqueId()), 
							PlayerJobs.filterStatus.get(p.getUniqueId())), 
					"");
			for(int i=0; i<gui.getSize(); i++) {
				if(i == 45) {
					//place netherstar
					gui.setItem(i, createPageItem(false));
				}
				else if(i == 49) {
					//place filter
					gui.setItem(i, createFilterItem(PlayerJobs.filterStatus.get(p.getUniqueId())));
				}
				else if(i == 53) {
					//place netherstar
					gui.setItem(i, createPageItem(true));
				}
				else if(gui.getItem(i) == null) {
					gui.setItem(i, createGlass());
				}
			}
		}
		else if(msg.equalsIgnoreCase("override decision")) {
			gui = plugin.getServer().createInventory(null, 9, ChatColor.GOLD + "Take Action");
			for(int i=0; i<gui.getSize(); i++) {
				//go back
				if(i == 0) {
					gui.setItem(i, createGoBack());
				}
				//finish job
				else if(i == 2) {
					gui.setItem(i, createDecision(Material.GREEN_CONCRETE, ChatColor.GREEN + "" + ChatColor.BOLD + "Click to set the job to finished!"));
				}
				//fire player
				else if(i == 5) {
					gui.setItem(i, createDecision(Material.ORANGE_CONCRETE, ChatColor.GOLD + "" + ChatColor.BOLD + "Click to fire the player working on the job!"));
				}
				//delete job
				else if(i == 8) {
					gui.setItem(i, createDecision(Material.RED_CONCRETE, ChatColor.RED + "" + ChatColor.BOLD + "Click to delete the job!"));
				}
				else {
					gui.setItem(i, createGlass());
				}
			}
		}
		
		if(gui != null) p.openInventory(gui);
	}
	
	private ItemStack createDecision(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	private void placeListings(Inventory gui, UUID playerUUID, ArrayList<Job> jobs, String s) {
		int page = PlayerJobs.pageCurrentlyOn.get(playerUUID);
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
						s));
			}
		}
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
	private ItemStack createJobDisplayItem(UUID uuid, UUID creator, String jobName, int payment, ArrayList<String> description, JobStatus status, UUID workingPlayer, String type) {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + jobName);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Job ID: " + ChatColor.GRAY + uuid.toString());
		lore.add(ChatColor.GOLD + "Creator: " + ChatColor.GRAY + Bukkit.getOfflinePlayer(creator).getName());
		lore.add(ChatColor.GOLD + "Payment: " + ChatColor.GRAY + payment + PlayerJobs.getEcon().currencyNameSingular());
		lore.add(description.get(0));
		description.remove(0);
		description.stream().forEachOrdered(desc -> lore.add(ChatColor.GRAY + desc));
		lore.add(ChatColor.GOLD + "Status: " + ChatColor.GRAY + status.toString());
		lore.add(ChatColor.GOLD + "Working player: " + ChatColor.GRAY + ((workingPlayer != null) ? Bukkit.getOfflinePlayer(workingPlayer).getName() : "-"));
		if(type.equalsIgnoreCase("created")) {
			lore.add(ChatColor.GOLD + (status == JobStatus.IN_PROGRESS ? "Click to finish the job!"
					: "Click to delete job!"));
		}
		else if(type.equalsIgnoreCase("accepted")) {
			lore.add(ChatColor.GOLD + "Click if you wish to quit this job!");
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createPageItem(boolean right) {
		ItemStack item = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + (right ? "Next" : "Previous") + " Page ");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack createFilterItem(JobStatus currentStatus) {
		ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Filter jobs");
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add("Currently filtering:");
		lore.add((currentStatus == JobStatus.ALL ? ChatColor.GOLD + "" + ChatColor.BOLD + ">>" : "" )+ ChatColor.GRAY + JobStatus.ALL.toString());
		lore.add((currentStatus == JobStatus.AWAITING ? ChatColor.GOLD + "" + ChatColor.BOLD + ">>" : "" )+ ChatColor.GRAY + JobStatus.AWAITING.toString());
		lore.add((currentStatus == JobStatus.IN_PROGRESS ? ChatColor.GOLD + "" + ChatColor.BOLD + ">>" : "" )+ ChatColor.GRAY + JobStatus.IN_PROGRESS.toString());
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
	
	public static void reducePageIfNeccessary(Player p, int size) {
		if(PlayerJobs.pageCurrentlyOn.get(p.getUniqueId()) != 1) {
			if(getTotalAmountOfExistingPages(size) <= PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())) {
				if((size) % PlayerJobs.GUI_PAGE_SIZE == 0) {
					PlayerJobs.pageCurrentlyOn.put(p.getUniqueId(), PlayerJobs.pageCurrentlyOn.get(p.getUniqueId())-1);
				}
			}
		}
		
	}
	
	public static int getTotalAmountOfExistingPages(int size) {
		return size % PlayerJobs.GUI_PAGE_SIZE == 0 ? size / PlayerJobs.GUI_PAGE_SIZE : (size % PlayerJobs.GUI_PAGE_SIZE) + 1;
	}
	
	public static boolean nextPageExists(UUID uuid, int size) {
		if(PlayerJobs.pageCurrentlyOn.containsKey(uuid)) {
			return PlayerJobs.pageCurrentlyOn.get(uuid) * PlayerJobs.GUI_PAGE_SIZE < size;
		}
		return false;
	}

}
