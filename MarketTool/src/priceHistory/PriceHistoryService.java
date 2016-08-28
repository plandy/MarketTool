package priceHistory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.ConnectionPool;
import database.PoolableConnection;
import database.sqlite.Procs;
import javafx.collections.ObservableList;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.DateUtility;

public enum PriceHistoryService {
	
	INSTANCE;
	
	public List<DataFeedTO> getPriceChartData( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> l_priceHistory = new ArrayList<DataFeedTO>();
		
		Date l_mostRecentDate = new Date(0);
		Date l_thisDate;
		Date l_todayDate = Date.from( ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York")).toInstant() );
		
		ConnectionPool pool = new ConnectionPool(1,1);
		PoolableConnection poolableConnection = null;
		try {
			poolableConnection = pool.requestConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( poolableConnection != null ) {
			try {
				PreparedStatement preparedStatement = poolableConnection.prepareStatement(Procs.S_PRICEHISTORY);
				preparedStatement.setString( 1, p_ticker );
				preparedStatement.setString( 2, "2011-02-02" );
				preparedStatement.setString( 3, "2012-08-03" );
				
				ResultSet results = preparedStatement.executeQuery();
				
				while ( results.next() ) {
					DataFeedTO dataTO = new DataFeedTO();
					
					dataTO.setTicker( results.getString("TICKER") );
					dataTO.setDate( results.getString("DATE") );
					dataTO.setOpenPrice( results.getBigDecimal("OPENPRICE") );
					dataTO.setHighPrice( results.getBigDecimal("HIGHPRICE") );
					dataTO.setLowPrice( results.getBigDecimal("LOWPRICE") );
					dataTO.setClosePrice( results.getBigDecimal("CLOSEPRICE") );
					dataTO.setVolume( results.getInt("VOLUME") );
					
					l_thisDate = DateUtility.parseStringToDate( results.getString("DATE") );
					
					if ( l_thisDate.after(l_mostRecentDate) ) {
						l_mostRecentDate = l_thisDate;
					}
				}
			} catch ( SQLException e ) {
				throw new RuntimeException();
				
			}
		}
		
		if ( l_mostRecentDate.before(l_todayDate) ) {
			YahooDataRequest l_rr = new YahooDataRequest( "IBM", l_mostRecentDate );
			ObservableList<DataFeedTO> l_missingHistory = l_rr.getPriceHistory();
		}
		
		return l_priceHistory;
		
	}

}
