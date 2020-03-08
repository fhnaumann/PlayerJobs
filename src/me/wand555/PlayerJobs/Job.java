package me.wand555.PlayerJobs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class Job {

	private static LinkedHashMap<UUID, Job> jobs = new LinkedHashMap<>();
	private UUID uuid;
	private UUID creator;
	private String jobName;
	private int jobPayment;
	private String jobDescription;
	private JobStatus status;
	private UUID workingPlayer;
	
	public Job(UUID uuid, UUID creator, String jobName, int jobPayment, String jobDescription) {
		this.uuid = uuid;
		this.creator = creator;
		this.jobName = jobName;
		this.jobPayment = jobPayment;
		this.jobDescription = jobDescription;
		this.status = JobStatus.AWAITING;
		this.setWorkingPlayer(null);
	}
	
	public Job(UUID uuid, UUID creator, String jobName, int jobPayment, String jobDescription, JobStatus status, UUID workingPlayer) {
		this.uuid = uuid;
		this.creator = creator;
		this.jobName = jobName;
		this.jobPayment = jobPayment;
		this.jobDescription = jobDescription;
		this.status = status;
		this.workingPlayer = workingPlayer;
	} 
	
	public static List<Job> getJobsPlayerAccepted(UUID workingPlayer) {
		return jobs.keySet().stream()
				.map(u -> Job.getJobFromUUID(u))
				.filter(j -> j.getWorkingPlayer() != null)
				.filter(j -> j.getWorkingPlayer().equals(workingPlayer)
						&& j.getStatus() == JobStatus.IN_PROGRESS)
				.collect(Collectors
						.toList());
	}
	
	public static boolean hasJobsCreated(UUID creator) {
		return jobs.keySet().stream()
				.map(u -> Job.getJobFromUUID(u))
				.anyMatch(j -> j.getStatus() == JobStatus.AWAITING 
					|| j.getStatus() == JobStatus.IN_PROGRESS);
	}
	
	public static List<Job> getJobsFromSpecificPlayer(UUID creator) {
		return jobs.keySet().stream()
				.map(u -> Job.getJobFromUUID(u))
				.filter(j -> j.getCreator().equals(creator) 
						&& j.getStatus() != JobStatus.FINISHED)
				.collect(Collectors
						.toList());
	}
	
	public static List<Job> getJobAsListFromSpecificPlayer(UUID creator, JobStatus status) {
		if(status == JobStatus.ALL) {
			return jobs.keySet().stream()
					.map(u -> Job.getJobFromUUID(u))
					.filter(j -> j.getCreator().equals(creator))
					.collect(Collectors
							.toList());
		}
		else {
			return jobs.keySet().stream()
					.map(u -> Job.getJobFromUUID(u))
					.filter(j -> j.getCreator().equals(creator)
							&& j.getStatus() == status)
					.collect(Collectors
							.toList());
		}
	}
	
	public static List<Job> getJobsAsList(JobStatus status) {
		if(status == JobStatus.ALL) {
			return jobs.keySet().stream()
					.map(u -> Job.getJobFromUUID(u))
					.collect(Collectors
							.toList());
		}
		else {
			return jobs.keySet().stream()
					.map(u -> Job.getJobFromUUID(u))
					.filter(j -> j.getStatus() == status)
					.collect(Collectors
							.toList());
		}
		
	}
	
	public static int getJobsAsListSize(JobStatus status) {
		if(status == JobStatus.ALL) {
			return jobs.size();
		}
		else {
			return (int) jobs.keySet().stream()
					.map(u -> Job.getJobFromUUID(u))
					.filter(j -> j.getStatus() == status)
					.count();
		}
	}
	
	public static Job getJobFromUUID(UUID uuid) {
		return jobs.getOrDefault(uuid, null);
	}
	
	public static void removeJobFromUUID(UUID uuid) {
		jobs.remove(uuid);
	}
	
	public static LinkedHashMap<UUID, Job> getJobs() {
		return jobs;
	}



	public static void setJobs(LinkedHashMap<UUID, Job> jobs) {
		Job.jobs = jobs;
	}



	public UUID getUuid() {
		return uuid;
	}



	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}



	public UUID getCreator() {
		return creator;
	}



	public void setCreator(UUID creator) {
		this.creator = creator;
	}



	public String getJobName() {
		return jobName;
	}



	public void setJobName(String jobName) {
		this.jobName = jobName;
	}



	public int getJobPayment() {
		return jobPayment;
	}



	public void setJobPayment(int jobPayment) {
		this.jobPayment = jobPayment;
	}



	public ArrayList<String> getJobDescription() {
		String s = WordUtils.wrap(ChatColor.GOLD + "Description: " + ChatColor.GRAY + jobDescription, 32, "_", false);
		return Stream.of(s.split("_")).collect(Collectors.toCollection(ArrayList::new));
	}

	public String getJobDescriptionAsSingleString() {
		return this.jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}



	public JobStatus getStatus() {
		return status;
	}



	public void setStatus(JobStatus status) {
		this.status = status;
	}



	public static void insertNewJob(Job job) {
		jobs.put(job.getUuid(), job);
	}



	public UUID getWorkingPlayer() {
		return workingPlayer;
	}



	public void setWorkingPlayer(UUID workingPlayer) {
		this.workingPlayer = workingPlayer;
	}
}
