package blonski_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	
private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		User user = new Gson().fromJson(request.getReader(), User.class);
		
		String username = user.username;
		String password = user.password;
		String email = user.email;
		int balance = user.balance;
		
		Gson gson = new Gson();
		
		if(username == null || username.isBlank() 
				|| password == null || password.isBlank()
				|| email == null || email.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		int userID = JDBCConnector.registerUser(username, password, email);
		
		if(userID == -1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username is taken";
			pw.write(gson.toJson(error));
			pw.flush();
		} else if(userID == -2) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Email is already registered";
			pw.write(gson.toJson(error));
			pw.flush();
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			pw.write(gson.toJson(userID));
			pw.flush();
		}
	}

}
