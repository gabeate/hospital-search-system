package HospitalSearchREST;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("hospitals")
public class HospitalResource {
	
	@GET
	@Path("/nearest")
	@Produces(MediaType.APPLICATION_JSON)
	public Hospital getNearestHospital(@QueryParam("lat") double userLat, @QueryParam("long") double userLong) {
		
		List<Hospital> hospitals = HospitalRepository.getInstance().getAllHospitals();
		
		Hospital nearest = null;
		double shortestDistance = Double.MAX_VALUE;
		
		for(Hospital h : hospitals) {
			double distance = h.distanceTo(userLat, userLong);
			if (distance < shortestDistance) {
				shortestDistance = distance;
				nearest = h;
			}
		}
		
		return nearest;
		
	}
	
	@GET
	@Path("/nearest")
	@Produces(MediaType.TEXT_HTML)
	public String getNearestHospitalHTML(@QueryParam("lat") double userLat, @QueryParam("long") double userLong) {
		
		
		Hospital h = getNearestHospital(userLat, userLong);
		
		if (h == null) {
			return "<html><head><link rel='stylesheet' href='/HospitalSearchSystemRESTProject/style.css'></head><body><h2>No hospitals found</h2>"
					+ "<p>There are no hospitals available right now.</p>"
					+ "<p><a href='../../NearestHospitalForm.html'>Search again</a></p></body></html>";
		}
		
		StringBuilder html = new StringBuilder();
		html.append("<html><head><link rel='stylesheet' href='/HospitalSearchSystemRESTProject/style.css'></head><body>");
		html.append("<h2>Nearest Hospital</h2>");
		html.append("<p>Your position: ").append(userLat).append(", ").append(userLong).append("</p>");
		html.append("<table>");
		html.append("<tr><th>ID</th><th>Name</th><th>Latitude</th><th>Longitude</th><th>Specialty</th><th>Distance</th></tr>");
		html.append("<tr>");
		html.append("<td>").append(h.getId()).append("</td>");
		html.append("<td>").append(h.getName()).append("</td>");
		html.append("<td>").append(h.getLatitude()).append("</td>");
		html.append("<td>").append(h.getLongitude()).append("</td>");
		html.append("<td>").append(h.getSpecialty()).append("</td>");
		html.append("<td>").append(String.format("%.2f km",  h.distanceTo(userLat, userLong))).append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<p><a href='../../NearestHospitalForm.html'>Search again</a></p>");
		html.append("<p><a href='/HospitalSearchSystemRESTProject/home.html'>Home</a></p>");
		html.append("</body></html>");
		
		return html.toString();
		
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getAllHospitals() {
		
		List<Hospital> hospitals = HospitalRepository.getInstance().getAllHospitals();
		
		StringBuilder html = new StringBuilder();
		html.append("<html><head><link rel='stylesheet' href='/HospitalSearchSystemRESTProject/style.css'></head><body>");
		html.append("<h2>Hospital List</h2>");
		html.append("<table>");
		html.append("<tr><th>ID</th><th>Name</th><th>Latitude</th><th>Longitude</th><th>Specialty</th></tr>");
		
		for (Hospital h : hospitals) {
			html.append("<tr>");
			html.append("<td>").append(h.getId()).append("</td>");
			html.append("<td>").append(h.getName()).append("</td>");
			html.append("<td>").append(h.getLatitude()).append("</td>");
			html.append("<td>").append(h.getLongitude()).append("</td>");
			html.append("<td>").append(h.getSpecialty()).append("</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		html.append("<p><a href='/HospitalSearchSystemRESTProject/home.html'>Home</a></p>");
		html.append("</body></html>");
		
		return html.toString();
	}
	
	@GET
	@Path("/specialty/{type}")
	@Produces(MediaType.TEXT_HTML)
	public String getHospitalsBySpecialty(@PathParam("type") String specialty) {
		
		List<Hospital> allHospitals = HospitalRepository.getInstance().getAllHospitals();
		List<Hospital> matches = new ArrayList<>();
		
		for (Hospital h : allHospitals) {
			if (h.getSpecialty().equalsIgnoreCase(specialty)) {
				matches.add(h);
			}
		}
		
		StringBuilder html = new StringBuilder();
		html.append("<html><head><link rel='stylesheet' href='/HospitalSearchSystemRESTProject/style.css'></head><body>");
		html.append("<h2>Hospitals with specialty: ").append(specialty).append("</h2>");
		html.append("<table>");
		html.append("<tr><th>ID</th><th>Name</th><th>Latitude</th><th>Longitude</th><th>Specialty</th></tr>");
		
		for (Hospital h : matches) {
			html.append("<tr>");
			html.append("<td>").append(h.getId()).append("</td>");
			html.append("<td>").append(h.getName()).append("</td>");
			html.append("<td>").append(h.getLatitude()).append("</td>");
			html.append("<td>").append(h.getLongitude()).append("</td>");
			html.append("<td>").append(h.getSpecialty()).append("</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		html.append("<p><a href='/HospitalSearchSystemRESTProject/home.html'>Home</a></p>");
		html.append("</body></html>");
		
		return html.toString();
	}
	
	@GET
	@Path("/searchSpecialty")
	@Produces(MediaType.TEXT_HTML)
	public String getHospitalsBySpecialtyParam(@QueryParam("specialty") String specialty) {
		return getHospitalsBySpecialty(specialty);
	}
}
