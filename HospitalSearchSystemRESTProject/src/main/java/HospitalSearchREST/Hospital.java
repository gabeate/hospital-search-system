package HospitalSearchREST;

public class Hospital {
	
	private int id;
	private String name;
	private double latitude;
	private double longitude;
	private String specialty;
	private static final int EARTH_RADIUS_KM = 6371;
	
	public Hospital() {
		super();
	}
	
	public Hospital(int id, String name, double latitude, double longitude, String specialty) {
		super();
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.specialty = specialty;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	
	public double distanceTo(double userLat, double userLong) {
		
		double x = Math.toRadians(userLong - this.longitude) * Math.cos(Math.toRadians((userLat + this.latitude) / 2));
		double y = Math.toRadians(userLat - this.latitude);
		
		return Math.sqrt(x * x + y * y) * EARTH_RADIUS_KM;
	}

	@Override
	public String toString() {
		return "Hospital [id=" + id + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", specialty=" + specialty + "]";
	}
	
	
}
