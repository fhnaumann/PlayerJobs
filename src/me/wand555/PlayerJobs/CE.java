package me.wand555.PlayerJobs;

import java.util.UUID;

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
		if(cmd.getName().equalsIgnoreCase("jobs")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					PlayerJobs.pageCurrentlyOn.put(player.getUniqueId(), 1);
					PlayerJobs.filterStatus.put(player.getUniqueId(), JobStatus.ALL);
					gui.createInventory(player, "jobs");
				}
			}
			else if(args.length == 4) {
				if(args[0].equalsIgnoreCase("create")) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(args[2].matches("-?\\d+")) {
							int amount = Integer.valueOf(args[2]);
							if(amount > 0) {
								
								//if(PlayerJobs.getEcon().has(player, player.getWorld().getName(), amount)) {
									PlayerJobs.getEcon().withdrawPlayer(player, player.getWorld().getName(), amount);
									Job.insertNewJob(new Job(UUID.randomUUID(), player.getUniqueId(), args[1], Integer.valueOf(args[2]), args[3]));
									player.sendMessage("Successfully created the job!");
								//}
							}	
						}
					}
				}
			}
		}
		
		return true;
	}

	
}
