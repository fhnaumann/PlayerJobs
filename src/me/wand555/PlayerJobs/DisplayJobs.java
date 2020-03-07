package me.wand555.PlayerJobs;

import java.util.ArrayList;
import java.util.UUID;

public class DisplayJobs {

	private ArrayList<Job> displayJobs = new ArrayList<Job>();
	private UUID playerOpenGUI;
	
	public DisplayJobs(UUID playerOpenGUI) {
		this.playerOpenGUI = playerOpenGUI;
	}
}
