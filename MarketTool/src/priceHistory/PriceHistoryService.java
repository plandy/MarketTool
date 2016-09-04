package priceHistory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import applicationConstants.InitialListedStocks;
import database.ConnectionPool;
import database.PoolableConnection;
import database.sqlite.Procs;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.DateUtility;

public enum PriceHistoryService {
	
	INSTANCE;
	
	private ConcurrentHashMap<String, Boolean> priceHistoryDataRequestCache = new ConcurrentHashMap<String, Boolean>(20,0.75f,2);
	
	/**
	 * used to return 2 objects from searchPriceHistory(). Find a nicer way of doing it.
	 */
	private class Pair{
		public List<DataFeedTO> priceHistory;
		public Date mostRecentDate;
		
		Pair( List<DataFeedTO> p_priceHistory, Date p_mostRecentDate ) {
			priceHistory = p_priceHistory;
			mostRecentDate = p_mostRecentDate;
		}
	}
	
	public List<DataFeedTO> getPriceChartData( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> l_priceHistory = new ArrayList<DataFeedTO>();
		
		Date l_mostRecentDate;
		Date l_todayDate = DateUtility.getTodayDate();
		
		Pair hack = searchPriceHistoryHack( p_ticker, p_beginDate, p_endDate );
		l_priceHistory = hack.priceHistory;
		l_mostRecentDate = hack.mostRecentDate;
		
		if ( l_mostRecentDate.before(l_todayDate) && !(priceHistoryDataRequestCache.containsKey("insertPrice"+p_ticker+DateUtility.parseDateToString(p_endDate))) ) {
			YahooDataRequest l_rr = new YahooDataRequest( p_ticker, l_mostRecentDate );
			List<DataFeedTO> l_missingHistory = l_rr.getPriceHistory();
			
			insertPriceHistory( p_ticker, l_missingHistory );
			
			l_priceHistory.addAll( l_missingHistory );
			
			priceHistoryDataRequestCache.putIfAbsent( "insertPrice"+p_ticker+DateUtility.parseDateToString(p_endDate), true );
		}
		
		return l_priceHistory;
		
	}
	
	/**
	 * Currently using a Pair to return the priceHistory plus mostRecentDate. The date is used to determine whether
	 * a dataRequest should be sent to retrieve up to date price information.
	 * <p> Don't want to iterate over the priceHistory twice just to get the date.
	 */
	private Pair searchPriceHistoryHack( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> l_priceHistory = new ArrayList<DataFeedTO>(400);
		Date l_mostRecentDate = p_beginDate;
		
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
				preparedStatement.setString( 2, DateUtility.parseDateToString(p_beginDate) );
				preparedStatement.setString( 3, DateUtility.parseDateToString(p_endDate) );
				
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
					
					l_priceHistory.add( dataTO );
					
					Date l_thisDate = DateUtility.parseStringToDate( results.getString("DATE") );
					if ( l_thisDate.after(l_mostRecentDate) ) {
						l_mostRecentDate = l_thisDate;
					}
				}
				
			} catch ( SQLException e ) {
				throw new RuntimeException();
			}
			
		}
		
		return new Pair( l_priceHistory, l_mostRecentDate );
	}
	
	private void insertPriceHistory( String p_ticker, List<DataFeedTO> p_priceHistory ) {
		
		if ( p_priceHistory.isEmpty() ) {
			return;
		}
		
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
				poolableConnection.beginTransaction();
				
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.I_PRICEHISTORY );
				
				for ( DataFeedTO dataTO : p_priceHistory ) {
					
					preparedStatement.setString(1, p_ticker );
					preparedStatement.setString(2, dataTO.getDate() );
					preparedStatement.setBigDecimal(3, dataTO.getOpenPrice());
					preparedStatement.setBigDecimal(4, dataTO.getHighPrice());
					preparedStatement.setBigDecimal(5, dataTO.getLowPrice());
					preparedStatement.setBigDecimal(6, dataTO.getClosePrice());
					preparedStatement.setInt(7, dataTO.getVolume());
					
					preparedStatement.addBatch();
					
				}
				
				int[] results = preparedStatement.executeBatch();
				
				poolableConnection.commitTransaction();
				
			} catch (SQLException e) {
				poolableConnection.silentRollback();
				e.printStackTrace();
			}
		}
	}
	
	public List<ListedStockTO> getListedStocks() {
		
		List<ListedStockTO> listedStocks = new ArrayList<ListedStockTO>(40);
		
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
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.GET_ALL_LISTEDSTOCKS );
				ResultSet results = preparedStatement.executeQuery();
				
				while ( results.next() ) {
					ListedStockTO stockTO = new ListedStockTO( results.getString("TICKER"), results.getString("FULLNAME") );
					listedStocks.add( stockTO );
				}
				
			} catch ( SQLException e ) {
				throw new RuntimeException();
			}
		}
		
		if ( listedStocks.isEmpty() ) {
			listedStocks = getInitialListedStocks();
		}
		
		return listedStocks;
	}
	
	public List<ListedStockTO> getInitialListedStocks() {
		return InitialListedStocks.listedStocks;
	}

}
