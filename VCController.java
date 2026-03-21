import java.util.*;

public class VCController {
	private Queue<Job> jobs = new LinkedList<>();
	
	public void assignJob(Job job) {
		jobs.add(job);
	}
	
	public String completion() {
		int time = 0;
		StringBuilder x = new StringBuilder();
		
		for(Job job : jobs) {
			time+= job.getDuration();
			
			x.append("Job ID: " + job.getJobID() + " | Duration: " + job.getDuration() +
	                  " | Completion Time: " + time + "\n");
	  
		}
		return x.toString();
	}
}
