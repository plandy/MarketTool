package priceHistory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.PoolableConnection;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.DateUtility;

public class PriceHistoryFacade extends DefaultFacade {
	
	public List<DataFeedTO> getPriceChartData( String p_ticker, Date p_beginDate, Date p_endDate ) {
		
		List<DataFeedTO> priceHistory = new ArrayList<DataFeedTO>();
		
		PoolableConnection poolableConnection = getDatabaseConnection();
		
		try {
			poolableConnection.beginTransaction();
			
			PriceHistoryService priceHistoryService = new PriceHistoryService();
			
			priceHistory = priceHistoryService.searchPriceHistory( p_ticker, p_beginDate, p_endDate, poolableConnection );
			
			Date mostRecentPriceDate = priceHistoryService.getMostRecentPricehistoryDate( p_ticker, poolableConnection );
			Date l_todayDate = DateUtility.getTodayDate();
			String mostRecentRequestDateString = priceHistoryService.getMostRecentRequestDate( p_ticker, poolableConnection );
			
			if ( mostRecentPriceDate.before(l_todayDate) ) {
				if ( mostRecentRequestDateString.isEmpty() || (!mostRecentRequestDateString.isEmpty() && DateUtility.beforeCalendarDate(DateUtility.parseStringToDate(mostRecentRequestDateString), l_todayDate)) ) {
					mostRecentPriceDate = DateUtility.addDays( mostRecentPriceDate, 1 );
					YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, mostRecentPriceDate );
					priceHistoryService.insertDataRequestHistory( p_ticker, l_todayDate, poolableConnection );
					List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
					priceHistoryService.insertPriceHistory( p_ticker, missingHistory, poolableConnection );
					priceHistory.addAll( missingHistory );
				}
			}
			
			poolableConnection.commitTransaction();
		} catch ( SQLException e ) {
			poolableConnection.silentRollback();
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
			
			Date beginDate = new Date(0);
			YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, beginDate );
			List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
			priceHistoryService.insertDataRequestHistory( p_ticker, DateUtility.getTodayDate(), poolableConnection );
			priceHistoryService.insertPriceHistory( p_ticker, missingHistory, poolableConnection );
			
			poolableConnection.commitTransaction();
		} catch ( SQLException e ) {
			poolableConnection.silentRollback();
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
		}
		
		return listedStocks;
	}

}
