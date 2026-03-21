import java.time.LocalDateTime;

public class Job {

    private String jobID;              
    private String clientID;             
    private LocalDateTime arrivalTime; 
    private LocalDateTime departureTime;
    private LocalDateTime deadline; 
    private int durationMinutes;     
   
    
    // Constructor
    public Job(String jobID, String clientID, LocalDateTime arrivalTime, LocalDateTime departureTime, int durationMinutes, LocalDateTime deadline ) {
        this.jobID = jobID;
        this.clientID = clientID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.durationMinutes = durationMinutes;
        this.deadline = deadline;
        

    }

    // returns job ID
    public String getJobID() {
        return jobID;
    }

    // returns client object
    public String getClientID() {
        return clientID;
    
    }

    // returns when job started
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
    	return departureTime;
    }
    
    public LocalDateTime getDeadline() {
    	return deadline;
    }
    
    // returns job duration in minutes
    public int getDurationMinutes() {
        return durationMinutes;
    }

    
    // formats job data for saving to file
    public String fileText() {
        return "Job ID: " + jobID +
               " | Client ID: " + clientID +
               " | Arrival Time: " + arrivalTime +
               " | Departure Time: " + departureTime+
               " | Deadline: " + deadline +
               " | Duration (min): " + durationMinutes;
   
    }
}