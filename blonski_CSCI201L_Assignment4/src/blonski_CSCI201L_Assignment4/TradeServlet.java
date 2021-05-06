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

@WebServlet("/TradeServlet")
public class TradeServlet extends HttpServlet {
	
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
		Date date = new Date(tr.dateInt);
		String type = tr.type;
		
		Gson gson = new Gson();
		
		if(userID == 0 || ticker == null || ticker.isBlank()
				|| numStock == 0 || price == 0 || date == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		double balance = JDBCConnector.retrieveBalance(userID);
		
		if(type.equals("sell")) {
			
			// find holding in portfolio and sell
			
			ArrayList<Trade> portfolio = JDBCConnector.retrievePortfolio(userID);
			
			
			for(int i = 0; i < portfolio.size(); i++) {
				Trade current = portfolio.get(i);
				
				double tradePrice = (price * numStock);
				if(current.ticker.equals(ticker)) {
					if(current.numStock < numStock) {
						// don't allow the user to trade
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						String error = "You can't sell more than you own.";
						pw.write(gson.toJson(error));
						pw.flush();
					} else {
						double ROI = JDBCConnector.executeSell(userID, tr);
						if(ROI != -1) {
							response.setStatus(HttpServletResponse.SC_OK);
							double currentBalance = JDBCConnector.updateBalance(userID, ROI);
							String newBalance = "SELL executed for an earning of $" + ROI + " leaving current balance of $" + currentBalance;
							pw.write(gson.toJson(newBalance));
							pw.flush();
						} else {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							String error = "Invalid trade - see executeSell";
							pw.write(gson.toJson(error));
							pw.flush();
						}
					}
				}
			}
		} else { // buy
			
			if((tr.numStock*tr.stockPrice) > balance) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				String error = "Trade cost exceeds your current balance";
				pw.write(gson.toJson(error));
				pw.flush();
			} else {
				JDBCConnector.insertIntoPortfolio(userID, tr);
				double balanceChange = (tr.numStock*tr.stockPrice);
				JDBCConnector.updateBalance(userID, -balanceChange);
			}
			
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		pw.write(gson.toJson("All set"));
		pw.flush();
		
	}
}
