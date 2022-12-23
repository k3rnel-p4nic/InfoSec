package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import db.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.improved.rsa.DigitalSignature;
import servlet.improved.rsa.RSA;
import servlet.improved.rsa.RSAKeys;
import servlet.improved.rsa.key.Key;
import servlet.improved.rsa.key.PrivateKey;
import servlet.improved.rsa.key.PublicKey;
import servlet.improved.rsa.key.RSAKeysAdapter;



/**
 * Servlet implementation class NavigationServlet
 */
@WebServlet("/NavigationServlet")
public class NavigationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String USER = DatabaseConfig.USERNAME;
	private static final String PWD = DatabaseConfig.PASSWORD;
	private static final String DRIVER_CLASS = DatabaseConfig.DRIVER;
	private static final String DB_URL = DatabaseConfig.URL;
    
	private static Connection conn;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NavigationServlet() {
        super();
    }
    
    /*protected void configure(HttpSecurity http) throws Exception {
        http
          .csrf().disable();
    }*/
    
    public void init() throws ServletException {
    	try {
			Class.forName(DRIVER_CLASS);
			
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", USER);
		    connectionProps.put("password", PWD);
	
	        conn = DriverManager.getConnection(DB_URL, connectionProps);
		        	
    	} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String email = request.getParameter("email").replace("'", "''");
		String pwd = request.getParameter("password").replace("'", "''");
				
		email = Jsoup.clean(email, Safelist.none());
		
		if (request.getParameter("newMail") != null)
			request.setAttribute("content", getHtmlForNewMail(email, pwd));
		else if (request.getParameter("inbox") != null)
			request.setAttribute("content", getHtmlForInbox(email));
		else if (request.getParameter("sent") != null)
			request.setAttribute("content", getHtmlForSent(email));
		else if (request.getParameter("search") != null)
			request.setAttribute("content", getHtmlForSearch(email));

		
		request.setAttribute("email", email);
		request.setAttribute("password", pwd);
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}
	
	public static String sanitize(String s) {
		return Jsoup.clean(s, Safelist.none());
	}

	
	private String getHtmlForSearch(String email) {
		String html = "<form action=\"SearchServlet\" method=\"GET\">"
				+ "<p>Insert a subject</p>"
				+ "<input name=\"subject\" type='string'>"
				+ "<input type=\"submit\">"
				+ "</form>";
		return html;
	}
	
	private int[] toArray(String s) {
		String[] r = s.split(",");
		int[] ret = new int[r.length];
		for (int i = 0; i < ret.length; ++i)
			ret[i] = Integer.parseInt(r[i].strip().replaceAll("\\[|\\]", ""));
		return ret;
	}
	
	private String getHtmlForInbox(String email) {
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
				"SELECT * FROM mail "
				+ "WHERE receiver='" + email + "'"
				+ "ORDER BY \"[time]\" DESC"
			);
			
			System.out.println("\t\tSELECT * FROM mail "
				+ "WHERE receiver='" + "ne@example.com" + "' OR receiver='" + email + "'"
				+ "ORDER BY \"[time]\" DESC");
			
			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");
			PrivateKey keys = PrivateKey.loadPrivateKeys(email);
			
			while (sqlRes.next()) {
				String message = sqlRes.getString(4);
				String sender = sqlRes.getString(1);
				PublicKey senderKeys = PublicKey.loadPublicKeys(sender);
				System.out.println("SPERIAMO VADA: " + senderKeys);
				int[] encryptedMessage = toArray(message);
				
				/* Sign */
				if (sqlRes.getBoolean(6))
					encryptedMessage = new DigitalSignature().readSigned(encryptedMessage, senderKeys);
					
				message = new RSA().decrypt(encryptedMessage, keys);
				
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("FROM:&emsp;" + sender + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
				output.append("</span>");
				output.append("<br><b>" + sanitize(sqlRes.getString(3)) + "</b>\r\n");
				output.append("<br>" + sanitize(message));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			}
			
			output.append("</div>");
			
			return output.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}
	
	private String getHtmlForNewMail(String email, String pwd) {
		return 
			"<form id=\"submitForm\" class=\"form-resize\" action=\"SendMailServlet\" method=\"post\">\r\n"
			+ "		<input type=\"hidden\" name=\"email\" value=\""+email+"\">\r\n"
			+ "		<input type=\"hidden\" name=\"password\" value=\""+pwd+"\">\r\n"
			+ "		<input class=\"single-row-input\" type=\"email\" name=\"receiver\" placeholder=\"Receiver\" required>\r\n"
			+ "		<input class=\"single-row-input\" type=\"text\"  name=\"subject\" placeholder=\"Subject\" required>\r\n"
			+ "		<textarea class=\"textarea-input\" name=\"body\" placeholder=\"Body\" wrap=\"hard\" required></textarea>\r\n"
			+ "		<input type=\"submit\" name=\"sent\" value=\"Send\">\r\n"
			+ "		<br>\r\n"
			+ "		<br>\r\n"
			+ "		<input type=\"checkbox\" name=\"sign\" value=\"true\">\r\n"
			+ "		<label for=\"signature\">Sign</label>\r\n"
			+ "	</form>";
	}
	
	private String getHtmlForSent(String email) {
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
				"SELECT * FROM mail "
				+ "WHERE sender='" + email + "'"
				+ "ORDER BY \"[time]\" DESC"
			);
			
			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");
			
			while (sqlRes.next()) {
				String message = sqlRes.getString(4);
				String receiver = sqlRes.getString(2);
				int[] encryptedMessage = toArray(message);
				
				if (sqlRes.getBoolean(6))
					encryptedMessage = new DigitalSignature().readSigned(encryptedMessage, PublicKey.loadPublicKeys(email));
				
				//Key ourKey = RSAKeys.loadKeys(receiver).getPrivateKeys();	// ☭
				//message = new RSA().decrypt(encryptedMessage, new RSAKeysAdapter(ourKey));
				
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("TO:&emsp;" + receiver + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
				output.append("</span>");
				output.append("<br><b>" + sanitize(sqlRes.getString(3)) + "</b>\r\n");
				output.append("<br>" + sanitize(message));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			}
			
			output.append("</div>");
			
			return output.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}
}
