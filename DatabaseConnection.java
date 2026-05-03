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

// Method for request table 
	public void insertRequest(String ownerId, String requestId, String vehicleId,
			String vehicleMake, String vehicleModel, int vehicleYear, String arrivalTime, String departureTime, LocalDateTime timestamp) {

		try {
			String sql = "INSERT INTO owner_requests"
			+ "(owner_id, request_id, vehicle_id, vehicle_make, "
			+ "vehicle_model, vehicle_year, arrival_time, departure_time, timestamp)" 
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, ownerId);
			ps.setString(2, requestId);
			ps.setString(3, vehicleId);       
			ps.setString(4, vehicleMake);
			ps.setString(5, vehicleModel);
			ps.setInt(6, vehicleYear);
			ps.setString(7, arrivalTime);
			ps.setString(8, departureTime);
			ps.setObject(9, timestamp);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clientInsert(String clientID, String requestId, LocalDateTime timestamp, Integer jobDuration,
			LocalDateTime jobDeadline) {
		String sql = "INSERT INTO client_requests "
        + "(client_id, request_id, job_duration, job_deadline, timestamp) "
        + "VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, clientID);
			ps.setString(2,requestId );
			ps.setObject(3, jobDuration);
			ps.setObject(4, jobDeadline);
			ps.setObject(5, timestamp);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
