package HospitalSearchREST;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/accounts")
public class AccountResource {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account createAccount(Account account) {
		
		Account created;
		
		try {
			created = AccountRepository.getInstance().addAccount(account);
		} catch (IllegalStateException e) {
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}
		
		if (created == null) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}
		
		return created;
	}
	
	@POST
	@Path("/addAccount")
	@Produces(MediaType.TEXT_HTML)
	public String createAccountFromForm(@FormParam("name") String name,
		@FormParam("email") String email,
		@FormParam("password") String password) {
			
			Account account;
			
			try {
				account = AccountRepository.getInstance().addAccount(new Account(0, name, email, password));
			} catch (IllegalStateException e) {
				return "<html><head><link rel='stylesheet' href='../../style.css'></head><body><h2>Account not created</h2>"
						+ "<p>The database is not available right now.</p>"
						+ "<p><a href='../../AddAccountForm.html'>Try again</a></p></body></html>";
			}
			
			StringBuilder html = new StringBuilder();
			html.append("<html><head><link rel='stylesheet' href='../../style.css'></head></body>");
			
			if (account == null) {
				html.append("<h2>Account not created</h2>");
				html.append("<p>This e-mail address is already registered.</p>");
			} else {
				html.append("<h2>Account created</h2>");
				html.append("<table border='1'>");
				html.append("<tr><th>ID</th><th>Name</th><th>Email</th></tr>");
				html.append("<tr>");
				html.append("<td>").append(account.getId()).append("</td>");
				html.append("<td>").append(account.getName()).append("</td>");
				html.append("<td>").append(account.getEmail()).append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
			
			html.append("<p><a href='../../AddAccountForm.html'>Create another account</a></p>");
			html.append("</body></html>");
			
			return html.toString();
		}
	
	// Login
	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public String login(@FormParam("email") String email, @FormParam("password") String password) {
		
		Account account = AccountRepository.getInstance().login(email, password);
		
		StringBuilder html = new StringBuilder();
		html.append("<html><head><link rel='stylesheet' href='../../style.css'></head></body>");
		
		if (account == null) {
			html.append("<h2>Login failed</h2>");
			html.append("<p>Invalid e-mail or password.</p>");
			html.append("<p><a href='../../LoginForm.html'>Try again</a></p>");
		} else {
			html.append("<h2>Welcome, ").append(account.getName()).append("!</h2>");
			html.append("<p>Account ID: ").append(account.getId()).append("</p>");
			html.append("<ul>");
			html.append("<li><a href='../../NearestHospitalForm.html'>Find the nearest hospital</a></li>");
			html.append("<li><a href='../../SpecialtyForm.html'>Search hospitals by specialty</a></li>");
			html.append("<li><a href='../hospitals'>View all hospitals</a></li>");
			html.append("<li><a href='../../ManageAccountForm.html?id=").append(account.getId())
				.append("'>Update or delete my account</a></li>");
			html.append("</ul>");
			html.append("<p><a href='../../index.html'>Log out</a></p>");
		}
		
		html.append("</body></html>");
		
		return html.toString();
	}
	
	// PUT .../rest/accounts?id=1&name=...&email=...&password=... (Test in Postman)
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	public String updateAccount(@QueryParam("id") int id,
			@QueryParam("name") String name,
			@QueryParam("email") String email,
			@QueryParam("password") String password) {
		
		int result;
		
		try {
			result = AccountRepository.getInstance().updateAccount(id, name, email, password);
		} catch (IllegalStateException e) {
			return "Update failed: " + e.getMessage();
		}
		
		if (result == 1) {
			return "Account " + id + " updated.";
		} else if (result == -1) {
			return "Update failed: this e-mail address is already registered.";
		}
		return "Account " + id + " was not found.";
	}
	
	// DELETE .../rest/accounts?id=1 (Test in Postman)
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteAccount(@QueryParam("id") int id) {
		
		try {
			if (AccountRepository.getInstance().deleteAccount(id)) {
				return "Account " + id + " and its appointments were deleted.";
			}
			return "Account " + id + " was not found.";
		} catch (IllegalStateException e) {
			return "Delete failed: " + e.getMessage();
		}
	}
}