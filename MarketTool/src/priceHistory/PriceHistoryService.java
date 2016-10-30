package priceHistory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import applicationConstants.InitialListedStocks;
import database.PoolableConnection;
import database.sqlite.procedures.ProcedureImplementations;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.DateUtility;

public class PriceHistoryService {
	
	ProcedureImplementations databaseProc = new ProcedureImplementations();
	
	public PriceHistoryTO getAllPriceHistory( String p_ticker, PoolableConnection p_connection ) throws SQLException {
		PriceHistoryTO priceHistory = new PriceHistoryTO();
			
		Date mostRecentPriceDate = databaseProc.getMostRecentPricehistoryDate( p_ticker, p_connection );
			
		if ( mostRecentPriceDate == null ) {
			initialisePriceHistory( p_ticker, p_connection );
		} else {
			getMissingHistoryFromDataFeed( p_ticker, mostRecentPriceDate, p_connection );
		}
			
		priceHistory = databaseProc.searchPriceHistory( p_ticker, new Date(0), DateUtility.getTodayDate(), p_connection );
		
		return priceHistory;
	}
	
	public void initialisePriceHistory( String p_ticker, PoolableConnection p_connection ) throws SQLException {
			
		Date beginDate = new Date(0);
		YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, beginDate );
		List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
		databaseProc.insertDataRequestHistory( p_ticker, DateUtility.getTodayDate(), p_connection );
		databaseProc.insertPriceHistory( p_ticker, missingHistory, p_connection );
		
	}
	
	public void getMissingHistoryFromDataFeed( String p_ticker, Date p_mostRecentPriceDate, PoolableConnection p_connection ) throws SQLException {
		
		Date mostRecentRequestDate = databaseProc.getMostRecentRequestDate( p_ticker, p_connection );
		Date todayDate = DateUtility.getTodayDate();
		if ( DateUtility.isBeforeCalendarDate(mostRecentRequestDate, todayDate) ) {
			p_mostRecentPriceDate = DateUtility.addDays( p_mostRecentPriceDate, 1 );
			YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, p_mostRecentPriceDate );
			databaseProc.insertDataRequestHistory( p_ticker, todayDate, p_connection );
			List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
			databaseProc.insertPriceHistory( p_ticker, missingHistory, p_connection );
		}
	}
	
	public List<ListedStockTO> getListedStocks( PoolableConnection p_connection ) throws SQLException {
		
		List<ListedStockTO> listedStocks = new ArrayList<ListedStockTO>();		

		listedStocks = databaseProc.getListedStocks( p_connection );

		return listedStocks;
	}
	
	public List<ListedStockTO> getInitialListedStocks() {
		return InitialListedStocks.listedStocks;
	}
	
}
