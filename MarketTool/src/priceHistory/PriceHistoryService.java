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
	
	public List<DataFeedTO> getPriceChartData( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> priceHistory = searchPriceHistory( p_ticker, p_beginDate, p_endDate );
		
		String mostRecentPriceDateString = getMostRecentPricehistoryDate( p_ticker );
		Date mostRecentPriceDate;
		if ( mostRecentPriceDateString.equals("") ) {
			//no price history exists, get history for the previous year from today
			mostRecentPriceDate = DateUtility.addYears( DateUtility.getTodayDate(), -1 );
		} else {
			mostRecentPriceDate = DateUtility.parseStringToDate( mostRecentPriceDateString );
			
		}
		
		Date l_todayDate = DateUtility.getTodayDate();
		String mostRecentRequestDateString = getMostRecentRequestDate( p_ticker );
		
		if ( mostRecentPriceDate.before(l_todayDate) ) {
			if ( mostRecentRequestDateString.isEmpty() || (!mostRecentRequestDateString.isEmpty() && DateUtility.beforeCalendarDate(DateUtility.parseStringToDate(mostRecentRequestDateString), l_todayDate)) ) {
				mostRecentPriceDate = DateUtility.addDays( mostRecentPriceDate, 1 );
				YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, mostRecentPriceDate );
				insertDataRequestHistory( p_ticker, l_todayDate );
				List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
				insertPriceHistory( p_ticker, missingHistory );
				priceHistory.addAll( missingHistory );
			}
		}
		
		return priceHistory;
		
	}
	
	private String getMostRecentRequestDate( String p_ticker ) {
		
		String mostRecentDate = "";
		
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
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.GET_MOSTRECENT_DATAREQUEST_DATE );
				preparedStatement.setString( 1, p_ticker );
				
				ResultSet results = preparedStatement.executeQuery();
				
				while ( results.next() ) {
					mostRecentDate = results.getString("REQUESTDATE");
				}
				if ( mostRecentDate == null ) {
					mostRecentDate = "";
				}
			} catch ( SQLException e ) {
				throw new RuntimeException();
			}
		}
		
		return mostRecentDate;
	}
	
	private String getMostRecentPricehistoryDate( String p_ticker ) {
		
		String dateString = "";
		
		ConnectionPool pool = new ConnectionPool(1,1);
		PoolableConnection poolableConnection = null;
		try {
			poolableConnection = pool.requestConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( poolableConnection != null ) {
			try  {
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.GET_MOSTRECENT_PRICEHISTORY_DATE );
				preparedStatement.setString( 1, p_ticker );
				
				ResultSet results = preparedStatement.executeQuery();
				
				while ( results.next() ) {
					dateString = results.getString("DATE");
					System.out.println("most recent date " +p_ticker+ " : " + dateString);
				}
				if ( dateString == null ) {
					dateString = "";
				}
			} catch ( SQLException e ) {
				throw new RuntimeException();
			}
		}
		
		return dateString;
		
	}
	
	private List<DataFeedTO> searchPriceHistory( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> priceHistory = new ArrayList<DataFeedTO>(400);
		
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
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.S_PRICEHISTORY );
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
					
					priceHistory.add( dataTO );
				}
				
			} catch ( SQLException e ) {
				throw new RuntimeException();
			}
			
		}
		
		return priceHistory;
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
	
	private void insertDataRequestHistory( String p_ticker, Date p_date ) {
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
				
				PreparedStatement preparedStatement = poolableConnection.prepareStatement( Procs.I_DATAREQUESTHISTORY );
				
				preparedStatement.setString(1, p_ticker );
				preparedStatement.setString(2, DateUtility.parseDateToString(p_date) );
				
				preparedStatement.addBatch();
				
				int[] results = preparedStatement.executeBatch();
				
				poolableConnection.commitTransaction();
			} catch (SQLException e) {
				poolableConnection.silentRollback();
			}
		}
	}

}
