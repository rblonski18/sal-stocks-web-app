package blonski_CSCI201L_Assignment4;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trade {
	
	public int tradeID;
	public String ticker;
	public int numStock;
	public double stockPrice;
	public Date purchaseDate;
	public long dateInt;
	public int userID;
	public String type;
	
	public double totalCost;
	public double avgCost;
	
	Trade(String tick, int user) {
		this.ticker = tick;
		this.userID = user;
	}
	
}
