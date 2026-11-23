package webHospitalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

import java.sql.ResultSet;

@WebService
@SOAPBinding(style = Style.RPC)
public class WebAccount {
	
	// Verify E-mail and Password
	@WebMethod
	public String login(String email, String password) {
		
		String sql = "SELECT id, name FROM account WHERE email = ? AND password = SHA2(?, 256)";
		
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, email);
			statement.setString(2, password);
			
			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return "Login successful. Welcome, " + rs.getString("name") + "! (Account ID: " + rs.getInt("id") + ")";
				}
			}
			
			return "Invalid e-mail address or password.";
			
		} catch (SQLException e) {
			System.out.println("Login error: " + e.getMessage());
			return "The login service is not available right now.";
		}
	}
}
