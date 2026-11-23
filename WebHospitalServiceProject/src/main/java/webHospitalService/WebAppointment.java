package webHospitalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebService
@SOAPBinding(style = Style.RPC)
public class WebAppointment {

	// Book an Appointment
	@WebMethod
	public String bookAppointment(int accountId, int hospitalId, String appointmentDate) {
		
		LocalDate date;
		
		try {
			date = LocalDate.parse(appointmentDate.trim());
		} catch (DateTimeParseException | NullPointerException e) {
			return "Invalid date. Please use the format yyyy-MM-dd.";
		}
		
		if (date.isBefore(LocalDate.now())) {
			return "The appointment date cannot be in the past.";
		}
		
		String sql = "INSERT INTO appointment (account_id, hospital_id, appointment_date) VALUES (?, ?, ?)";
		
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setInt(1, accountId);
			statement.setInt(2, hospitalId);
			statement.setString(3, date.toString());
			statement.executeUpdate();
			
			int appointmentId = 0;
			
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					appointmentId = keys.getInt(1);
				}
			}
			
			return "Appointment " + appointmentId + " confirmed for " + date + " (account " + accountId + ", hospital " + hospitalId + ").";
		} catch (SQLIntegrityConstraintViolationException e) {
			return "Unknown account ID or hospital ID.";
		} catch (SQLException e) {
			System.out.println("Booking error: " + e.getMessage());
			return "The appointment service is not available right now.";
		}
	}
	
	// List Appointments for One Account
	@WebMethod
	public String displayAppointments(int accountId) {
		
		String sql = "SELECT a.id, h.name, a.appointment_date "
				+ "FROM appointment a JOIN hospital h ON a.hospital_id = h.id "
				+ "WHERE a.account_id = ? ORDER BY a.appointment_date, a.id";
		
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, accountId);
			
			String result = "";
			int count = 0;
			
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					result += "appointment_Id: " + rs.getInt("id")
					+ ", hospital: " + rs.getString("name")
					+ ", date: " + rs.getString("appointment_date") + "\n";
					count++;
				}
			}
			
			if (count == 0 ) {
				return "No appointments found for account " + accountId + ".";
			}
			
			result += "\nTotal appointments: " + count;
			return result;
			
		} catch (SQLException e ) {
			System.out.println("Appointment list error: " + e.getMessage());
			return "The appointment service is not available right now.";
		}
	}
}
