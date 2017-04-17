package priceHistory.dataFeed.yahooFinance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import priceHistory.dataFeed.DataFeedTO;
import utility.DateUtility;
import utility.logger.Log;

public class YahooDataRequest {
	
	private List<DataFeedTO> priceHistory = new ArrayList<DataFeedTO>();
	
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
		
		Date l_todayDate = DateUtility.getTodayDate();		
		Date l_beginDate = DateUtility.addYears( l_todayDate, -1 );
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, l_beginDate, l_todayDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		Log.info( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	public YahooDataRequest( String p_ticker, Date p_beginDate ) {
		
		Date l_todayDate = DateUtility.getTodayDate();
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, p_beginDate, l_todayDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		Log.info( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	public YahooDataRequest( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker, p_beginDate, p_endDate );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		Log.info( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	/**
	 * 
	 * Possible FileNotFound Exception: example: if most recent request was friday, and today is sunday, no 
	 * data will exist for saturday since there is no trading, and no file will be returned.
	 * <p>
	 * format of date: MM-dd-yyy
	 * 
	 * @param p_ticker
	 * @param p_beginDate
	 * @param p_endDate
	 * @return
	 */
	private InputStreamReader dataRequest( String p_ticker, Date p_beginDate, Date p_endDate ){
		
		//yahoo finance indexes months from 0-11
		String[] dateTokens = dateTokenizer( p_beginDate );
		String beginDateURL = "&a="+ String.valueOf((Integer.parseInt(dateTokens[1]) - 1)) +"&b="+dateTokens[2]+"&c="+dateTokens[0];
		
		dateTokens = dateTokenizer( p_endDate );
		String endDateURL = "&d="+ String.valueOf((Integer.parseInt(dateTokens[1]) - 1)) +"&e="+dateTokens[2]+"&f="+dateTokens[0];
		
		try{
			
//			/**
//			 * example url: l_url = 
//			  		new URL("http://ichart.finance.yahoo.com/table.csv?s=IBM&a=01&b=21&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv");
//			 * 
//			 */

			URL l_url = null;
			l_url = new URL("https://ichart.finance.yahoo.com/table.csv?s="+ p_ticker + beginDateURL+ endDateURL +"g=d&ignore=.csv");
			
			Log.info( "Yahoo Data request: " + p_ticker + DateUtility.parseDateToString( p_beginDate ) + DateUtility.parseDateToString( p_endDate ) );
			Instant start = Instant.now();
			
			URLConnection l_connection = l_url.openConnection();
			((HttpURLConnection) l_connection).getResponseCode();
			InputStreamReader l_stream = new InputStreamReader( l_connection.getInputStream() );
			
			Instant end = Instant.now();
			Log.info("Connection establish duration: " + Duration.between(start, end).getNano());
			
			return l_stream;
			
		} catch ( FileNotFoundException e ) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;	
	}
	
	private void readData( InputStreamReader p_inputStream, String p_ticker ){
		if ( p_inputStream == null ) {
			return;
		}
		
		List<DataFeedTO> l_priceHistory = new ArrayList<DataFeedTO>();
		
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
			Log.info("Data request parse duration: " + Duration.between(start, end).getNano());
			
			setPriceHistory(l_priceHistory);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
			
	}

	public List<DataFeedTO> getPriceHistory() {
		return priceHistory;
	}

	private void setPriceHistory (List<DataFeedTO> priceHistory) {
		this.priceHistory = priceHistory;
	}
	
	/**
	 * first formats the input date. Then parse date to string.
	 * 
	 * @param p_date
	 * @return
	 */
	private String[] dateTokenizer( Date p_date ) {
		
		String beginDateString = DateUtility.parseDateToString( p_date );
		String[] tokens = beginDateString.split( "-" );
		
		return tokens;
	}

}
