import java.time.LocalDateTime;

public class Client extends user {

    private int jobDurationMinutes;
    private LocalDateTime jobDeadline;

// clientID is passed to the parent class(user) as the ID 
    public Client(String clientID, int jobDurationMinutes, LocalDateTime jobDeadline) {
        super(clientID);
        this.jobDurationMinutes = jobDurationMinutes;
        this.jobDeadline = jobDeadline;
    }

    public int getJobDurationMinutes() {
        return jobDurationMinutes;
    }

    public LocalDateTime getJobDeadline() {
        return jobDeadline;
    }

    @Override
    public String fileText() {
        return "Client ID: " + ID
                + " | Timestamp: " + time
                + " | Approx job duration (min): " + jobDurationMinutes
                + " | Job deadline: " + jobDeadline;
    }
}