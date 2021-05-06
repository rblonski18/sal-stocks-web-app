package blonski_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Trade tr = new Gson().fromJson(request.getReader(), Trade.class);
		
		int userID = tr.userID;
		String ticker = tr.ticker;
		int numStock = tr.numStock;
		double price = tr.stockPrice;
		Date date = null;
		
		try {
			date = new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("dateBought"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		
		if(userID == 0 || ticker == null || ticker.isBlank()
				|| numStock == 0 || price == 0 || date == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		Trade trade = new Trade(ticker, userID);
		trade.numStock = numStock;
		trade.stockPrice = price;
		trade.purchaseDate = date;
		
		JDBCConnector.insertIntoPortfolio(userID, trade);
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		int userID = Integer.parseInt(request.getParameter("userID"));
		String type = request.getParameter("type");
		
		Gson gson = new Gson();
		
		if(userID == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		if(type.equals("balance")) {
			response.setStatus(HttpServletResponse.SC_OK);
			pw.write(gson.toJson(JDBCConnector.retrieveBalance(userID)));
			pw.flush();
			return;
		}
		
		ArrayList<Trade> favorites = JDBCConnector.retrievePortfolio(userID);
		
		response.setStatus(HttpServletResponse.SC_OK);
		pw.write(gson.toJson(favorites));
		pw.flush();
	}

}
