package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import db.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.improved.password.Hasher;
import servlet.improved.rsa.RSAKeys;
import servlet.improved.rsa.RSAKeysGenerator;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String USER = DatabaseConfig.USERNAME;
	private static final String PWD = DatabaseConfig.PASSWORD;
	private static final String DRIVER_CLASS = DatabaseConfig.DRIVER;
	private static final String DB_URL = DatabaseConfig.URL;
	    
	private static Connection conn;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }
    
    public void init() throws ServletException {
    	try {
			Class.forName(DRIVER_CLASS);
			
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", USER);
		    connectionProps.put("password", PWD);
	
			System.out.println(USER);
	        conn = DriverManager.getConnection(DB_URL, connectionProps);
		        	
    	} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		// The replacement escapes apostrophe special character in order to store it in SQL
		String name = request.getParameter("name").replace("'", "''");
		String surname = request.getParameter("surname").replace("'", "''");
		String email = request.getParameter("email").replace("'", "''");
		String pwd = request.getParameter("password").replace("'", "''");
		
		name = NavigationServlet.sanitize(name);
		surname = NavigationServlet.sanitize(surname);
		email = NavigationServlet.sanitize(email);
		pwd = NavigationServlet.sanitize(pwd);
		
		
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
				"SELECT * "
				+ "FROM \"[user]\" "
				+ "WHERE email='" + email + "'"
			);
			
			if (sqlRes.next()) {
				System.out.println("Email already registered!");
				request.getRequestDispatcher("register.html").forward(request, response);
				
			} else {
				String salt = Hasher.getSalt();
				pwd = Hasher.hashString(pwd, salt);
				st.execute(
					"INSERT INTO \"[user]\" ( name, surname, email, password ) "
					+ "VALUES ( '" + name + "', '" + surname + "', '" + email + "', '" + pwd + "' )"
				);
				
				System.out.println(String.format("INSERT INTO saltpwd (password, salt) VALUES ('%s', '%s')", pwd, salt));
				st.execute(String.format("INSERT INTO saltpwd (password, salt) VALUES ('%s', '%s')", pwd, salt));
				
				request.setAttribute("email", email);
				request.setAttribute("password", pwd);
				
				System.out.println("Registration succeeded!");
				
				RSAKeys keys = new RSAKeysGenerator().generateKeys();
				keys.savePublicKeys(email);
				keys.savePrivateKeys(email);
				System.out.println(keys);
				
				request.setAttribute("rsa-e", keys.getE());
				request.setAttribute("rsa-d", keys.getD());
				request.setAttribute("rsa-n", keys.getN());
				
				request.getRequestDispatcher("home.jsp").forward(request, response);
			}
			
		} catch (SQLException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			request.getRequestDispatcher("register.html").forward(request, response);
		}
	}

}
