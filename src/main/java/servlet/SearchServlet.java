package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import db.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Connection conn;
	
	public SearchServlet() {
		super();
	}
	
    public void init() throws ServletException {
    	try {
			Class.forName(DatabaseConfig.DRIVER);
			
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", DatabaseConfig.USERNAME);
		    connectionProps.put("password", DatabaseConfig.PASSWORD);
	
	        conn = DriverManager.getConnection(DatabaseConfig.URL, connectionProps);    	
    	} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String subject = req.getParameter("subject");
		subject = Jsoup.clean(subject, Safelist.none());
		
		System.out.println("GET: " + subject);
		
		req.setAttribute("email", getServletContext().getAttribute("email"));
		
		resp.setContentType("text/html");	
		req.setAttribute("content", getMailsBySubject(subject));
		req.getRequestDispatcher("home.jsp").forward(req, resp);
	}
	
	
	private String getMailsBySubject(String subject) {
		try (Statement st = conn.createStatement()) {
			ResultSet res = st.executeQuery("SELECT * FROM mail WHERE subject = '" + subject + "' ORDER BY \"[time]\" DESC");
			System.out.println("SELECT * FROM mail WHERE sender = '" + subject + "' ORDER BY \"[time]\" DESC");
			StringBuilder output = new StringBuilder();
			output.append("<p>Searching mails from: " + subject + "</p>");
			output.append("<div>\r\n");
			
			while (res.next()) {
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("FROM:&emsp;" + res.getString(1) + "&emsp;&emsp;AT:&emsp;" + res.getString(5));
				output.append("</span>");
				output.append("<br><b>" + res.getString(3) + "</b>\r\n");
				output.append("<br>" + res.getString(4));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			}
			
			output.append("</div>");
			return output.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR IN FETCHING MAILS FROM: " + subject + "!";

		}
	}
	
}
