package blonski_CSCI201L_Assignment4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JDBCConnector {
	
	// returns userID if user is found, otherwise returns -1.
	public static int loginUser(String user, String pass) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int userID = -1;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			String username = user;
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Users WHERE username='" + username + "' AND pass='" + pass + "'");
			while(rs.next()) {
				userID = rs.getInt("userID");
			}
		} catch(SQLException sqle) {
			System.out.println("SQLE in loginUser. ");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return userID;
	}
	
	// tries to register user - returns user id if successful, -1 if username taken, -2 if email taken
	public static int registerUser(String username, String password, String email) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		int userID = -1;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Users WHERE username='" + username + "'");
			if(!rs.next()) { // no user with that username
				st = conn.createStatement();
				rs = st.executeQuery("SELECT * FROM Users WHERE email='" + email + "'");
				if(!rs.next()) {
					// no user with that email either
					rs.close();
					st.execute("INSERT INTO Users (username, pass, email, balance) VALUES ('" + username + "', '" + password + "','" + email + "', 50000)");
					rs = st.executeQuery("SELECT LAST_INSERT_ID()");
					rs.next();
					userID = rs.getInt(1); // andrew has getInt(1) here -> try if this doesn't work. 
				} else {
					// this email is taken. 
					userID = -2;
				}
			}
		
		} catch(SQLException sqle) {
			System.out.println("SQLException in registerUser. ");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return userID;
	}
	
	// retrieve favorites
	
	public static ArrayList<Trade> getUserFavorites(int userID) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		String ticker = null;
		int userIDPlaceholder = 0;
		ArrayList<Trade> result = new ArrayList<Trade>();
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Favorites WHERE userID=" + userID);
			while(rs.next()) {
				userIDPlaceholder = rs.getInt("userID");
				ticker = rs.getString("ticker");
				
				result.add(new Trade(ticker, userID));
			}
			
		} catch(SQLException sqle) {
			System.out.println("SQLException in getUserFavorites. ");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return result;
	}
	// insert into favorites for user
	
	public static void insertIntoFavorites(int userID, String ticker) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			st.execute("INSERT INTO Favorites (userID, ticker) VALUES (" + userID + ", '" + ticker + "')");
		} catch(SQLException sqle) {
			System.out.println("SQLException in insertIntoFavorites");
			sqle.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
	}
	
	// remove trade from favorites table. 
	public static void removeFromFavorites(int userID, String ticker) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			st.execute("DELETE FROM Favorites WHERE userID=" + userID + " AND ticker='" + ticker + "'");
		} catch(SQLException sqle) {
			System.out.println("SQLException in removeFromFavorites. ");
			sqle.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}
	
	// insert trade into portfolio
	public static void insertIntoPortfolio(int userID, Trade tr) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			st.execute("INSERT INTO Portfolio (userID, ticker, numStock, price, dateBought) VALUES (" + userID + ", '" + tr.ticker + "', " + 
			tr.numStock + ", " + tr.stockPrice + ", "+ tr.dateInt+ ")");
		} catch(SQLException sqle) {
			System.out.println("SQLException in insertIntoPortfolio");
			sqle.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}
	
	// retrieve portfolio
	public static ArrayList<Trade> retrievePortfolio(int userID) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		String ticker = null;
		int number = 0;
		double price = 0;
		long purchaseDate = 0;
		int userIDPlaceholder = 0;
		ArrayList<Trade> holder = new ArrayList<Trade>();
		ArrayList<Trade> result = new ArrayList<Trade>();
		Set<String> hash = new HashSet<String>();
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Portfolio WHERE userID=" + userID);
			while(rs.next()) {
				userIDPlaceholder = rs.getInt("userID");
				ticker = rs.getString("ticker");
				number = rs.getInt("numStock");
				price = rs.getDouble("price");
				purchaseDate = rs.getLong("dateBought");
				
				Trade tr = new Trade(ticker, userID);
				tr.numStock = number;
				tr.stockPrice = price;
				tr.dateInt = purchaseDate;
				holder.add(tr);
			}
			
			for(int i = 0; i < holder.size(); i++) {
				if(hash.contains(holder.get(i).ticker)) {
					for(int j = 0; j < result.size(); j++) {
						if(result.get(j).ticker.equals(holder.get(i).ticker)) {
							result.get(j).totalCost += (holder.get(i).numStock * holder.get(i).stockPrice);
							result.get(j).numStock += holder.get(i).numStock;
							result.get(j).avgCost = (result.get(j).totalCost / result.get(j).numStock);
						}
					}
				} else {
					Trade current = holder.get(i);
					current.totalCost = (current.numStock * current.stockPrice);
					current.avgCost = (current.totalCost / current.numStock);
					result.add(current);
					hash.add(holder.get(i).ticker);
				}
			}
			
		} catch(SQLException sqle) {
			System.out.println("SQLException in retrievePortfolio ");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return result;
	}
	
	public static double retrieveBalance(int userID) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		double balance = 0;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Users WHERE userID=" + userID);
			while(rs.next()) {
				balance = rs.getDouble("balance");
			}
			
		} catch(SQLException sqle) {
			System.out.println("SQLException in retrieveBalance");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return balance;
		
	}
	
	// update user's balance, get -1 if invalid, currentBalance if valid
	public static double updateBalance(int userID, double balanceChange) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		double currentBalance = 0;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Users WHERE userID=" + userID);
			while(rs.next()) {
				currentBalance = rs.getDouble("balance");
			}
			
			currentBalance += balanceChange;
			
			int res = 0;
			if(currentBalance < 0) {
				return -1;
			} else {
				conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
				st = conn.createStatement();
				res = st.executeUpdate("UPDATE Users SET balance=" + currentBalance + " WHERE userID=" + userID);
			}
			
		} catch(SQLException sqle) {
			System.out.println("SQLException in updateBalance");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return currentBalance;
		
	}
	
	// returns -1 if invalid trade, otherwise returns total money earned from trade
	public static double executeSell(int userID, Trade tr) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		String ticker = null;
		int number = 0;
		double price = 0;
		long purchaseDate = 0;
		int tradeID = 0;
		int shares = 0;
		int userIDPlaceholder = -1;
		ArrayList<Trade> result = new ArrayList<Trade>();
		
		int tradeShares = tr.numStock;
		double returnOnInvestment = 0;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Portfolio WHERE userID=" + userID + " AND ticker='" + tr.ticker + "'");
			while(rs.next()) {
				userIDPlaceholder = rs.getInt("userID");
				tradeID = rs.getInt("tradeID");
				ticker = rs.getString("ticker");
				number = rs.getInt("numStock");
				price = rs.getDouble("price");
				purchaseDate = rs.getLong("dateBought");
				
				Trade trade = new Trade(ticker, userID);
				trade.numStock = number;
				trade.stockPrice = price;
				trade.dateInt = purchaseDate;
				trade.tradeID = tradeID;
				result.add(trade);
			}
			
			Collections.sort(result, new Comparator<Trade>(){
				   public int compare(Trade t1, Trade t2){
				      return (int) (t1.dateInt - t2.dateInt);
				   }
				});
			
			Collections.reverse(result);
			
			System.out.println(result);
			
			int numShares = 0;
			for(int i = 0; i < result.size(); i++) {
				numShares += result.get(i).numStock;
			}
			
			int res = 0;
			if(numShares < tr.numStock) {
				// can't allow user to execute trade.
				return -1;
			} else {
				
				for(int i = 0; i < result.size(); i++) {
					if(tradeShares >= result.get(i).numStock) {
						// sell shares is larger than the current buy shares, 
						returnOnInvestment += ((result.get(i).numStock*tr.stockPrice));
						System.out.println(result.get(i).numStock);
						System.out.println(tr.stockPrice);
						System.out.println("1: Trade Shares: " + tradeShares + " : " + returnOnInvestment);
						tradeShares -= result.get(i).numStock;
						conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
						st = conn.createStatement();
						st.execute("DELETE FROM Portfolio WHERE tradeID=" + result.get(i).tradeID + " AND userID=" + tr.userID);
						if(tradeShares == 0) break;
					} else {
						// tradeShares = 0;
						returnOnInvestment += ((tradeShares*tr.stockPrice));
						System.out.println(tr.stockPrice);
						System.out.println("2: Trade Shares: " + tradeShares + " : " + returnOnInvestment);
						conn = DriverManager.getConnection("jdbc:mysql://localhost/ass4?user=root&password=root");
						st = conn.createStatement();
						int remaining = result.get(i).numStock - tradeShares;
						res = st.executeUpdate("UPDATE Portfolio SET numStock=" + remaining + " WHERE tradeID=" + result.get(i).tradeID);
						break;
					}
				}
				
			}
			
			
			
		} catch(SQLException sqle) {
			System.out.println("SQLException in executeSell. ");
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return returnOnInvestment;
		
	}

}