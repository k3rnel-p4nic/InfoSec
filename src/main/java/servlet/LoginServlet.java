package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

/**
 * Servlet implementation class HelloWorldServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String USER = DatabaseConfig.USERNAME;
	private static final String PWD = DatabaseConfig.PASSWORD;
	private static final String DRIVER_CLASS = DatabaseConfig.DRIVER;
	private static final String DB_URL = DatabaseConfig.URL;
	
    
	private static Connection conn;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }
    
    public void init() throws ServletException {
    	try {
			Class.forName(DRIVER_CLASS);
			
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", USER);
		    connectionProps.put("password", PWD);
	
	        conn = DriverManager.getConnection(DB_URL, connectionProps);
		    
		    System.out.println("User \"" + USER + "\" connected to database.");
    	
    	} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String email = request.getParameter("email");
		String pwd = request.getParameter("password");
		String query = "SELECT email, s.password,salt FROM \"[user]\" u JOIN saltpwd s ON s.password = u.password  WHERE email=?";
		
		System.out.println(pwd);
		
		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, email);
			ResultSet sqlRes = st.executeQuery();
			
			if (sqlRes.next()) {
				String retrievedSaltedPassword = sqlRes.getString(2);
				String salt = sqlRes.getString(3);
				
				if (Hasher.validateWithHashedString(pwd, retrievedSaltedPassword, salt)) {
					request.setAttribute("email", email);
					this.getServletContext().setAttribute("email", sqlRes.getString(3));
					request.setAttribute("password", retrievedSaltedPassword);
					
					System.out.println("Login succeeded!");
					
					request.setAttribute("content", "");
					request.getRequestDispatcher("home.jsp").forward(request, response);
				} else {
					System.out.println("Does not match!");
					request.getRequestDispatcher("login.html").forward(request, response);

				}
			} else {
				System.out.println("Login failed!");
				request.getRequestDispatcher("login.html").forward(request, response);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			request.getRequestDispatcher("login.html").forward(request, response);
		}
	}
}
