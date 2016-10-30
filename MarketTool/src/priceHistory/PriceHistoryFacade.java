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

public class PriceHistoryFacade extends DefaultFacade {
	
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

}
