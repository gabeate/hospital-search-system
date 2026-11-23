package HospitalSearchREST;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HospitalRepository {
	
	private static HospitalRepository instance;
	
	private HospitalRepository() {
	}
	
	public static HospitalRepository getInstance() {
		if (instance == null) {
			instance = new HospitalRepository();
		}
		return instance;
	}
	
	public List<Hospital> getAllHospitals() {
		
		List<Hospital> hospitals = new ArrayList<>();
		
		String sql = "SELECT h.id, h.name, h.latitude, h.longitude, s.name AS specialty "
				+ "FROM hospital h JOIN specialty s ON h.specialty_id = s.id ORDER BY h.id";
		
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery()) {
			
			while (rs.next()) {
				hospitals.add(new Hospital(rs.getInt("id"), 
						rs.getString("name"), 
						rs.getDouble("latitude"), 
						rs.getDouble("longitude"),
						rs.getString("specialty")));
			}
			
		} catch (SQLException e) {
			System.out.println("Error reading hospitals: " + e.getMessage());
		}
		
		return hospitals;
	}
}