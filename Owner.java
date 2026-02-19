public class Owner extends user{
	
	private String vehicleID;
	private String vehicleModel;
	private String vehicleMake;
	private int vehicleYear;
	private String residencyTime;
	
	public Owner(String ownerID, String vehicleID, String vehicleModel, String vehicleMake, int vehicleYear, String residencyTime) {
		super(ownerID);
		this.vehicleID = vehicleID;
		this.vehicleModel = vehicleModel;
		this.vehicleMake = vehicleMake;
		this.vehicleYear = vehicleYear;
		this.residencyTime = residencyTime;
	}
	@Override
	public String fileText() {
		return "Owner ID: " + ID + " | Timestamp: " + time " | Vehicle ID: " + vehicleID + " | Vehicle model: " + vehicleModel + " | Vehicle make: " + vehicleMake +
				" | Vehicle year: " + vehicleYear + " | Estimated residency time: " + residencyTime;
	}
}