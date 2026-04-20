import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DatabaseConnection {

	private Connection conn; // Saving the database connection into varible "conn"

	public DatabaseConnection() { // Constuctor , connects that datbase connection
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vcrts_db", "root", "password");
		} catch (SQLException e) {
			throw new RuntimeException("Database connection failed", e);
		}
	}

	public Connection getConnection() {// Returns the connection
		return conn;
	}

	// Method for inserting into User Table that action listner will call
	public void insertUser(String userId, String userType) {
		try {
			String sql = "INSERT INTO users (user_id, user_type) VALUES (?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, userId);
			ps.setString(2, userType);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

// Method for request table 
	public void insertRequest(String requestId, String userId, LocalDateTime timestamp, String vehicleId,
			String vehicleMake, String vehicleModel, int vehicleYear, String arrivalTime, String departureTime,
			Integer jobDuration, LocalDateTime jobDeadline) {

		try {
			String sql = "INSERT INTO requests (request_id, user_id, timestamp, job_duration, job_deadline) " + "VALUES (?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, requestId);
			ps.setString(2, userId);
			ps.setObject(3, timestamp);

			ps.setString(4, vehicleId);
			ps.setString(5, vehicleMake);
			ps.setString(6, vehicleModel);
			ps.setInt(7, vehicleYear);

			ps.setString(8, arrivalTime);
			ps.setString(9, departureTime);

			ps.setObject(10, jobDuration);
			ps.setObject(11, jobDeadline);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clientInsert(String requestID, String userID, LocalDateTime timestamp, Integer jobDuration,
			LocalDateTime jobDeadline) {
		String sql = "INSERT INTO requests " + "(request_id, user_id, timestamp, job_duration, job_deadline)"
				+ "VALUES ( ?, ?, ? ,?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, requestID);
			ps.setString(2, userID);
			ps.setObject(3, timestamp);

			ps.setObject(4, jobDuration);
			ps.setObject(5, jobDeadline);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
