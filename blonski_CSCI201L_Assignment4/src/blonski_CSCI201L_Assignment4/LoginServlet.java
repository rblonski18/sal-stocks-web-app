package blonski_CSCI201L_Assignment4; 

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		User user = new Gson().fromJson(request.getReader(), User.class);
		
		String username = user.username;
		String password = user.password;
		String email = user.email;
		String type = user.type;
		
		Gson gson = new Gson();
		
		if(username == null || username.isBlank() || password == null || password.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username or password missing";
			
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		if(type.equals("Google")) {
			Integer userID2 = JDBCConnector.loginUser(username, password);
			if(userID2 == -1) {
				int userID3 = JDBCConnector.registerUser(username, password, email);
				response.setStatus(HttpServletResponse.SC_OK);
				pw.write(gson.toJson(userID3));
				pw.flush();
				return;
			}
		}
		
		Integer userID = JDBCConnector.loginUser(username, password);
		
		if(userID == -1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username or password incorrect";
			pw.write(gson.toJson(error));
			pw.flush();
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			pw.write(gson.toJson(userID));
			pw.flush();
		}
	}
}
