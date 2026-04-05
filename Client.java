import java.time.LocalDateTime;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.*;
import java.util.List;

public class Client extends User {

	private int jobDurationMinutes;
	private LocalDateTime jobDeadline;
	private final String requestID;

// clientID is passed to the parent class(user) as the ID 
	public Client(String clientID, int jobDurationMinutes, LocalDateTime jobDeadline) {
		super(clientID);
		this.jobDurationMinutes = jobDurationMinutes;
		this.jobDeadline = jobDeadline;
		this.requestID = clientID + "-" + System.currentTimeMillis();
	}


	public int getJobDurationMinutes() {
		return jobDurationMinutes;
	}

	public LocalDateTime getJobDeadline() {
		return jobDeadline;
	}

	@Override
	public String fileText() {
		return "Request ID: " + requestID + " | Client ID: " + ID + " | Timestamp: " + time + " | Approx job duration (min): " + jobDurationMinutes
				+ " | Job deadline: " + jobDeadline;
		}
	   public String jobRequest(String host, int port) {
	        try (Socket client = new Socket(host, port);
	             DataOutputStream dos = new DataOutputStream(client.getOutputStream());
	             DataInputStream dis = new DataInputStream(client.getInputStream())) {

	            String job = fileText();
	            dos.writeUTF(job);
	            dos.flush();

	            String message = dis.readUTF();
	            System.out.println("Message: " + message);

	            String result = dis.readUTF();
	            System.out.println("Result: " + result);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "ERROR";
	        }
	    }
	}

