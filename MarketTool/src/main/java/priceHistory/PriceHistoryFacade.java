package priceHistory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.PoolableConnection;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.DateUtility;

public class PriceHistoryFacade extends AbstractFacade {
	
	public PriceHistoryTO getAllPriceHistory( String p_ticker ) {
		PriceHistoryTO priceHistory = new PriceHistoryTO();
		
		PoolableConnection poolableConnection = getDatabaseConnection();
		
		try {
			poolableConnection.beginTransaction();
			
			PriceHistoryService priceHistoryService = new PriceHistoryService();
			
			priceHistory = priceHistoryService.getAllPriceHistory( p_ticker, poolableConnection );
			
			poolableConnection.commitTransaction();
		} catch ( SQLException e ) {
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}
		
		return priceHistory;
	}
	
	/**
	 * If the database has no priceHistory for an entity, this is called to request and insert all historic data up to 1970.
	 */
	public void initialisePriceHistory( String p_ticker ) {
		
		PoolableConnection poolableConnection = getDatabaseConnection();
		
		try {
			poolableConnection.beginTransaction();
			
			PriceHistoryService priceHistoryService = new PriceHistoryService();
			priceHistoryService.initialisePriceHistory( p_ticker, poolableConnection );
			
			poolableConnection.commitTransaction();
		} catch ( SQLException e ) {
			poolableConnection.silentRollback();
		} finally {
			poolableConnection.returnToPool();
		}
		
	}
	
	public List<ListedStockTO> getListedStocks() {
		
		List<ListedStockTO> listedStocks = new ArrayList<ListedStockTO>();
		
		PoolableConnection poolableConnection = getDatabaseConnection();
		
		try {
			PriceHistoryService priceHistoryService = new PriceHistoryService();
			listedStocks = priceHistoryService.getListedStocks( poolableConnection );
		} catch ( SQLException e ) {
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}
		
		return listedStocks;
	}
	
	public List<DataFeedTO> getHistoryFromDataFeed( String p_ticker ) {
		PoolableConnection poolableConnection = getDatabaseConnection();
		List<DataFeedTO> history;
		
		try {
			poolableConnection.beginTransaction();
			
			PriceHistoryService priceHistoryService = new PriceHistoryService();
			history = priceHistoryService.getMissingHistoryFromDataFeed( p_ticker, poolableConnection );
			
			poolableConnection.commitTransaction();
		} catch ( SQLException e ) {
			poolableConnection.silentRollback();
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}

		return history;
	}

	public void updateWatchlist( ListedStockTO p_stock ) {
		PoolableConnection poolableConnection = getDatabaseConnection();

		try {
			poolableConnection.beginTransaction();

			PriceHistoryService priceHistoryService = new PriceHistoryService();
			priceHistoryService.updateWatchlist(p_stock, poolableConnection);

			poolableConnection.commitTransaction();
		} catch (SQLException e) {
			poolableConnection.silentRollback();
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}
	}

	public List<DataFeedTO> getMissingHistoryFromDataFeed( String p_ticker ) {
		List<DataFeedTO> missingHistory;

		PoolableConnection poolableConnection = getDatabaseConnection();
		PriceHistoryService priceHistoryService = new PriceHistoryService();

		Date mostRecentPriceDate = null;
		Date mostRecentRequestDate = null;

		try {
			mostRecentPriceDate = priceHistoryService.getMostRecentPricehistoryDate( p_ticker, poolableConnection );
			mostRecentRequestDate = priceHistoryService.getMostRecentRequestDate( p_ticker, poolableConnection );
		} catch ( SQLException e ) {
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}

		missingHistory = priceHistoryService.getMissingHistoryFromDataFeed( p_ticker, mostRecentPriceDate, mostRecentRequestDate );

		return missingHistory;
	}

	public void insertPriceHistory( String p_ticker, List<DataFeedTO> p_history ) {
		PoolableConnection poolableConnection = getDatabaseConnection();
		PriceHistoryService priceHistoryService = new PriceHistoryService();

		try{
			poolableConnection.beginTransaction();

			priceHistoryService.insertPriceHistory( p_ticker, p_history, poolableConnection );
			priceHistoryService.insertDataRequestHistory( p_ticker, DateUtility.getTodayDate(), poolableConnection );

			poolableConnection.commitTransaction();
		} catch (SQLException e) {
			poolableConnection.silentRollback();
			throw new RuntimeException();
		} finally {
			poolableConnection.returnToPool();
		}

	}

}
