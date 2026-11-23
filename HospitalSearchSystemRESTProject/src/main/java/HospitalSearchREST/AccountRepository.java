package HospitalSearchREST;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

	private static AccountRepository instance;

	private AccountRepository() {
	}

	public static AccountRepository getInstance() {
		if (instance == null) {
			instance = new AccountRepository();
		}
		return instance;
	}

	// Add account
	public Account addAccount(Account account) {

		String sql = "INSERT INTO account (name, email, password) VALUES (?, ?, SHA2(?, 256))";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, account.getName());
			statement.setString(2, account.getEmail());
			statement.setString(3, account.getPassword());
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					account.setId(keys.getInt(1));
				}
			}

			account.setPassword(null);
			return account;

		} catch (SQLIntegrityConstraintViolationException e) {
			return null;
		} catch (SQLException e) {
			System.out.println("Error adding account: " + e.getMessage());
			throw new IllegalStateException("Database not available", e);
		}
	}

	public List<Account> getAllAccounts() {

		List<Account> accounts = new ArrayList<>();

		String sql = "SELECT id, name, email FROM account ORDER BY id";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery()) {

			while (rs.next()) {
				accounts.add(new Account(rs.getInt("id"), rs.getString("name"), rs.getString("email"), null));
			}

		} catch (SQLException e) {
			System.out.println("Error reading accounts: " + e.getMessage());
		}

		return accounts;
	}

	// Account Validation
	public Account login(String email, String password) {

		String sql = "SELECT id, name, email FROM account WHERE email = ? AND password = SHA2(?, 256)";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, email);
			statement.setString(2, password);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return new Account(rs.getInt("id"), rs.getString("name"), rs.getString("email"), null);
				}
			}

		} catch (SQLException e) {
			System.out.println("Error during login: " + e.getMessage());
		}

		return null;
	}

	// Update Account
	public int updateAccount(int id, String name, String email, String password) {

		String sql = "UPDATE account SET "
				+ "name = COALESCE(NULLIF(?, ''), name), "
				+ "email = COALESCE(NULLIF(?, ''), email), "
				+ "password = IF(? IS NULL OR ? = '', password, SHA2(?, 256)) "
				+ "WHERE id = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, name);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.setString(4, password);
			statement.setString(5, password);
			statement.setInt(6, id);

			return statement.executeUpdate();

		} catch (SQLIntegrityConstraintViolationException e) {
			return -1;
		} catch (SQLException e) {
			System.out.println("Error updating account: " + e.getMessage());
			throw new IllegalStateException("Database error: " + e.getMessage(), e);
		}
	}

	// Delete Account
	public boolean deleteAccount(int id) {

		String sql = "DELETE FROM account WHERE id = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, id);
			return statement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error deleting account: " + e.getMessage());
			throw new IllegalStateException("Database error: " + e.getMessage(), e);
		}
	}
}