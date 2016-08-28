package priceHistory.dataFeed.yahooFinance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import applicationConstants.StringConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import priceHistory.dataFeed.DataFeedTO;

public class YahooDataRequest {
	
	private ObservableList<DataFeedTO> priceHistory;
	
	/** return format of yahoo finance request:
	 *  <br> Date,Open,High,Low,Close,Volume,Adj Close
		<br> 2013-02-19,14.45,14.47,14.19,14.25,10895700,12.780314
		<p>
	*	<br> example URL format:
	*	<br> http://ichart.finance.yahoo.com/table.csv?s=IBM&a=01&b=01&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv
	*	<br> a=begin month, b=begin day, c=begin year; d,e,f are the end date components
	*
	*/
	public YahooDataRequest( String p_ticker ) {
		
		Date l_todayDate = Date.from( ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York")).toInstant() );
		Date l_beginDate = null;
		
		String l_beginDateString = "2011-01-01";
		try {
			l_beginDate = new SimpleDateFormat( StringConstants.DATE_FORMAT ).parse(l_beginDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, l_beginDate, l_todayDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		System.out.println( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	public YahooDataRequest( String p_ticker, Date p_beginDate ) {
		
		Date l_todayDate = Date.from( ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York")).toInstant() );
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, p_beginDate, l_todayDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		System.out.println( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	public YahooDataRequest( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, p_beginDate, p_endDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		System.out.println( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	private InputStreamReader dataRequest( String p_ticker, Date p_beginDate, Date p_endDate ){
		
		String[] dateTokens = dateTokenizer( p_beginDate );
		String beginDateURL = "&a="+dateTokens[1]+"&b="+dateTokens[2]+"&c="+dateTokens[0];
		
		dateTokens = dateTokenizer( p_endDate );
		String endDateURL = "&d="+dateTokens[1]+"&e="+dateTokens[2]+"&f="+dateTokens[0];
		
		try{
			
//			/**
//			 * example url: l_url = 
//			  		new URL("http://ichart.finance.yahoo.com/table.csv?s=IBM&a=01&b=21&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv");
//			 * 
//			 */

			URL l_url = null;
			l_url = new URL("http://ichart.finance.yahoo.com/table.csv?s="+ p_ticker + beginDateURL+ endDateURL +"g=d&ignore=.csv");
			
			Instant start = Instant.now();
			
			URLConnection l_connection = l_url.openConnection();
			InputStreamReader l_stream = new InputStreamReader(l_connection.getInputStream());
			
			Instant end = Instant.now();
			System.out.println("Connection establish duration: " + Duration.between(start, end).getNano());
			
			return l_stream;
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private void readData( InputStreamReader p_inputStream, String p_ticker ){
		
		ArrayList<DataFeedTO> list = new ArrayList<DataFeedTO>();
		ObservableList<DataFeedTO> l_priceHistory = FXCollections.observableList( list );
		
		try {
			
			Instant start = Instant.now();
			
			BufferedReader l_buff = new BufferedReader( p_inputStream );
			
			String line1 = null;
			
			//first line gives column headers
			line1 = l_buff.readLine();
			
			while ( (line1 = l_buff.readLine()) != null ) {
				
				String[] values = line1.split(",");
				
				DataFeedTO dataObject = new DataFeedTO();
				
				dataObject.setTicker( p_ticker );
				dataObject.setDate( values[0] );
				dataObject.setOpenPrice( new BigDecimal(values[1]) );
				dataObject.setHighPrice(  new BigDecimal(values[2]) );
				dataObject.setLowPrice(  new BigDecimal(values[3]) );
				dataObject.setClosePrice(  new BigDecimal(values[4]) );
				dataObject.setVolume( Integer.parseInt(values[5]) );
				
				l_priceHistory.add( dataObject );
				
			}
			
			Instant end = Instant.now();
			System.out.println("Data request parse duration: " + Duration.between(start, end).getNano());
			
			setPriceHistory(l_priceHistory);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
			
	}

	public ObservableList<DataFeedTO> getPriceHistory() {
		return priceHistory;
	}

	private void setPriceHistory (ObservableList<DataFeedTO> priceHistory) {
		this.priceHistory = priceHistory;
	}
	
	private String[] dateTokenizer( Date p_date ) {
		String beginDateString = new SimpleDateFormat( StringConstants.DATE_FORMAT ).format( p_date );
		String[] tokens = beginDateString.split("-");
		
		return tokens;
	}

}
