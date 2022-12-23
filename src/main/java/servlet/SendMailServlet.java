package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import db.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
 * Servlet implementation class SendMailServlet
 */
@WebServlet("/SendMailServlet")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USER = DatabaseConfig.USERNAME;
	private static final String PWD = DatabaseConfig.PASSWORD;
	private static final String DRIVER_CLASS = DatabaseConfig.DRIVER;
	private static final String DB_URL = DatabaseConfig.URL;
    
	private static Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String sender = request.getParameter("email").replace("'", "''");;
		String receiver = request.getParameter("receiver").replace("'", "''");;
		String subject = request.getParameter("subject").replace("'", "''");;
		String body = request.getParameter("body").replace("'", "''");;
		String timestamp = new Date(System.currentTimeMillis()).toInstant().toString();
		boolean signature_required = request.getParameter("sign") != null;

		RSA r = new RSA();
		PublicKey keys = PublicKey.loadPublicKeys(receiver);
		PrivateKey myKeys = PrivateKey.loadPrivateKeys(sender);
		int[] encryptedBody = r.encrypt(body, keys);
		
		if (signature_required)
			encryptedBody = new DigitalSignature().sign(encryptedBody, myKeys);
		
		body = Arrays.toString(encryptedBody);
		System.out.println(body);
		
		try (Statement st = conn.createStatement()) {
			st.execute(
				"INSERT INTO mail ( sender, receiver, subject, body, \"[time]\", signed ) "
				+ "VALUES ( '" + sender + "', '" + receiver + "', '" + subject + "', '" + body + "', '" + timestamp + "', '" + signature_required + "' )"
			);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("email", sender);
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}

}
