package blonski_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/FavoritesServlet")
public class FavoritesServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// handle insert into favorites
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
		Trade tr = new Gson().fromJson(request.getReader(), Trade.class);
		
		int userID = tr.userID;
		String ticker = tr.ticker;
		String type = tr.type;
		
		Gson gson = new Gson();
		
		if(userID == 0 || ticker == null || ticker.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		if(type.equals("delete")) JDBCConnector.removeFromFavorites(userID, ticker);
		else JDBCConnector.insertIntoFavorites(userID, ticker);
		
		response.setStatus(HttpServletResponse.SC_OK);
		pw.write(gson.toJson("all good"));
		pw.flush();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		int userID = Integer.parseInt(request.getParameter("userID"));
		
		Gson gson = new Gson();
		
		if(userID == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		ArrayList<Trade> favorites = JDBCConnector.getUserFavorites(userID);
		
		response.setStatus(HttpServletResponse.SC_OK);
		pw.write(gson.toJson(favorites));
		pw.flush();
	}

}
