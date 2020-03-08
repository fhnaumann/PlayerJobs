package me.wand555.PlayerJobs;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE implements CommandExecutor {

	private PlayerJobs plugin;
	private GUIClasses gui;
	
	public CE(PlayerJobs plugin, GUIClasses gui) {
		this.plugin = plugin;
		this.gui = gui;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("jobs")) {
				if(args.length == 0) {
					if(player.hasPermission("jobs.jobs")) {
						PlayerJobs.pageCurrentlyOn.put(player.getUniqueId(), 1);
						PlayerJobs.filterStatus.put(player.getUniqueId(), JobStatus.ALL);
						gui.createInventory(player, "jobs");
					}		
				}
				else {
					player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Syntax: " + ChatColor.GOLD + "/jobs" + ChatColor.GRAY + " to open the GUI.");
				}
			}
			else if(cmd.getName().equalsIgnoreCase("jobscreate")) {
				if(args.length >= 3) {
					String name = "";
					int amount = 1;
					String desc = "";
					if(args[0].startsWith("[") && args[args.length-1].endsWith("]")) {
						setValues(player, args, name, amount, desc);
					}
					else {
						player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Syntax: " + 
							ChatColor.GOLD + "/jobscreate <[name]> <amount> <[description]>");
						player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "The '" + 
							ChatColor.GOLD + "[]" + ChatColor.GRAY + "' are necessary!");
					}
									
				}
			}
			else if(cmd.getName().equalsIgnoreCase("jobsoverride")) {
				if(args.length == 1) {
					if(player.hasPermission("jobs.override.*")) {
						@SuppressWarnings("deprecation")
						UUID creator = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
						if(Job.hasJobsCreated(creator)) {
							PlayerJobs.pageCurrentlyOn.put(player.getUniqueId(), 1);
							PlayerJobs.overridejobs.put(player.getUniqueId(), creator);
							PlayerJobs.filterStatus.put(player.getUniqueId(), JobStatus.ALL);
							gui.createInventory(player, "jobsoverride");
						}
						else {
							player.sendMessage(PlayerJobs.PREFIX + ChatColor.GOLD + args[0] + ChatColor.GRAY + " has no jobs created!");
						}
					}		
				}
				else {
					player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Syntax: " + ChatColor.GOLD + "/jobsoverride <player>" + ChatColor.GRAY + " to open the admin GUI.");
				}
			}
		}
		//from console
		else {
			System.out.println("Commands from the plugin 'PlayerJobs' only work ingame!");
		}		
		return true;
	}

	private void setValues(Player player, String[] args, String name, int amount, String desc) {
		int pointer = 0;
		boolean inbetween = false;
		for(int i=pointer; i<args.length-2; i++) {
			//when the 'word' is not on the edge
			if(inbetween) {
				//needed to confirm above
				if(!args[i].endsWith("]")) {
					name += (args[i] + " ");
					pointer++;
				}	
			}
			//when word is on left edge
			if(args[i].startsWith("[")) {
				//when word is also on the right edge (e.g. only one word in total)
				if(args[i].endsWith("]")) {
					name += args[i].substring(1, args[i].length()-1);
					pointer++;
					break;
				}
				//when word is only on the left, not on the right
				else {
					inbetween = true;
					name += (args[i].substring(1) + " ");
					pointer++;
				}
				
			}
			//when word is on the right
			else if(args[i].endsWith("]")) {
				inbetween = false;
				name += args[i].substring(0, args[i].length()-1);
				pointer++;
				break;
			}	
		}		
		if(args[pointer].matches("-?\\d+")) {
			amount = Integer.valueOf(args[pointer]);
			pointer++;
		}
		else {
			player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Has to be a valid amount!");
		}
		
		for(int i=pointer; i<args.length; i++) {
			//when the 'word' is not on the edge
			if(inbetween) {
				//needed to confirm above
				if(!args[i].endsWith("]")) {
					desc += (args[i] + " ");
					pointer++;
				}	
			}
			//when word is on left edge
			if(args[i].startsWith("[")) {
				//when word is also on the right edge (e.g. only one word in total)
				if(args[i].endsWith("]")) {
					desc += args[i].substring(1, args[i].length()-1);
					pointer++;
					break;
				}
				//when word is only on the left, not on the right
				else {
					inbetween = true;
					//System.out.println("name: " + name + " appending: " + args[i].substring(1));
					desc += (args[i].substring(1) + " ");
					pointer++;
				}
				
			}
			//when word is on the right
			else if(args[i].endsWith("]")) {
				inbetween = false;
				desc += args[i].substring(0, args[i].length()-1);
				pointer++;
				break;
			}	
		}	
		if(amount > 0) {
			if(PlayerJobs.getEcon().has(player, player.getWorld().getName(), amount)) {
				if(player.hasPermission("jobs.create")) {
					PlayerJobs.getEcon().withdrawPlayer(player, player.getWorld().getName(), amount);
					Job.insertNewJob(new Job(UUID.randomUUID(), player.getUniqueId(), name, amount, desc));
					player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Successfully created the job!");
				}		
			}
			else {
				player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "You don't have enough money!");
			}
		}
		else {
			player.sendMessage(PlayerJobs.PREFIX + ChatColor.GRAY + "Amount has to be greater than 0!");
		}
	}
	

	
}
