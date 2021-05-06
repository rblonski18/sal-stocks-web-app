package blonski_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/TiingoProxyServlet")
public class TiingoProxyServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Gson gson = new Gson();
		String json = null;
		
		String ticker = request.getParameter("ticker");
		String date = null;
		date = request.getParameter("dateBought");
		String type = request.getParameter("type");
		
		if(ticker == null || ticker.isBlank() || date == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Trade info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		String url1 = "https://api.tiingo.com/tiingo/daily/" + ticker + 
				"?token=50d4d927db8d992794daab2956218a0384f75434";
		
		String url2 = "https://api.tiingo.com/tiingo/daily/" + ticker + 
				"/prices?startDate=" + date + "&endDate=" + date + "&token=50d4d927db8d992794daab2956218a0384f75434";
		
		String url3 = "https://api.tiingo.com/iex?tickers=" + ticker + "&token=50d4d927db8d992794daab2956218a0384f75434";
		
		String url = "";
		if(type.equals("info")) url = url1;
		else if(type.equals("change")) url = url3;
		else url = url2;
		
		try {
			URL actualURL = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) actualURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			json = buffer.toString();
			reader.close();
			
		} catch(NoSuchFileException e) {
			
			System.out.println("The ticker price on " + date + " could not be found. ");
			System.out.println("Please reformat the schedule to pass in valid dates. ");
			
		} catch(IOException e) {
			
			System.out.println("The file is not formatted properly. There was trouble parsing the file. ");
	
		} 
		System.out.println(json);
		response.setStatus(HttpServletResponse.SC_OK);
		pw.write(gson.toJson(json));
		pw.flush();
	}

}
