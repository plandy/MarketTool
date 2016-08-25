package priceHistory.dataFeed.yahooFinance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import priceHistory.dataFeed.DataFeedTO;

public class YahooDataRequest {
	
	private ObservableList<DataFeedTO> priceHistory;
	
	/** return format of yahoo finance request:
	 *  <br> Date,Open,High,Low,Close,Volume,Adj Close
		<br> 2013-02-19,14.45,14.47,14.19,14.25,10895700,12.780314  */
	public YahooDataRequest( String p_ticker ) {
		
		Instant start = Instant.now();
		
		InputStreamReader l_reader = dataRequest( p_ticker );
		readData( l_reader, p_ticker );
		
		Instant end = Instant.now();
		
		System.out.println( "Total data request duration: " + Duration.between(start, end).getNano() );
		
	}
	
	public InputStreamReader dataRequest( String p_ticker ){
		
		try{
			
			URL l_url = null;
			//l_url = new URL("http://ichart.finance.yahoo.com/table.csv?s=WU&a=01&b=19&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv");
			l_url = new URL("http://ichart.finance.yahoo.com/table.csv?s="+ p_ticker +"&a=01&b=19&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv");
			//l_url = new URL("http://real-chart.finance.yahoo.com/table.csv?s=CSV&a=7&b=9&c=1996&d=7&e=13&f=2016&g=d&ignore=.csv");
			l_url = new URL("http://ichart.finance.yahoo.com/table.csv?s="+ p_ticker +"&a=01&b=01&c=2010&d=01&e=19&f=2016&g=d&ignore=.csv");
			
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
	
	public void readData( InputStreamReader p_inputStream, String p_ticker ){
		
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
				
				//DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
				//dataObject.setDate( dateFormat.parse(values[0]) );
				
				//System.out.println( dataObject.getDate() );
				
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

	public void setPriceHistory(ObservableList<DataFeedTO> priceHistory) {
		this.priceHistory = priceHistory;
	}

}
