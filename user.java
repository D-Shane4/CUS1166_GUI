import java.time.LocalDateTime;

public abstract class user {
	protected String ID;
	protected LocalDateTime time;

	public user(String ID) {
		this.ID = ID;
		this.time = LocalDateTime.now();
	}

	// returns user ID
	public String getID() {
		return ID;
	}

	// returns time stamp of user transaction
	public LocalDateTime getTime() {
		return time;
	}

	public abstract String fileText();

}
