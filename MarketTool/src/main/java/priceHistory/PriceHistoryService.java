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

		List<DataFeedTO> missingHistory = getMissingHistoryFromDataFeed( p_ticker, p_connection );
		databaseProc.insertPriceHistory( p_ticker, missingHistory, p_connection );
			
		priceHistory = databaseProc.searchPriceHistory( p_ticker, new Date(0), DateUtility.getTodayDate(), p_connection );
		
		return priceHistory;
	}

	public List<DataFeedTO> getMissingHistoryFromDataFeed( String p_ticker, PoolableConnection p_connection ) throws SQLException {

		List<DataFeedTO> missingHistory = new ArrayList<DataFeedTO>();

		Date mostRecentPriceDate = getMostRecentPricehistoryDate( p_ticker, p_connection );
		Date mostRecentRequestDate = getMostRecentRequestDate( p_ticker, p_connection );
		Date todayDate = DateUtility.getTodayDate();

		if ( DateUtility.isBeforeCalendarDate(mostRecentRequestDate, todayDate) ) {
			mostRecentPriceDate = DateUtility.addDays( mostRecentPriceDate, 1 );
			YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, mostRecentPriceDate );
			databaseProc.insertDataRequestHistory( p_ticker, todayDate, p_connection );
			missingHistory = yahooDataRequest.getPriceHistory();
		}

		return missingHistory;
	}

	public List<DataFeedTO> getMissingHistoryFromDataFeed( String p_ticker, Date p_mostRecentPriceDate, Date p_mostRecentRequestDate ) {

		List<DataFeedTO> missingHistory = new ArrayList<DataFeedTO>();

		if ( DateUtility.isBeforeCalendarDate(p_mostRecentRequestDate, DateUtility.getTodayDate()) ) {
			p_mostRecentPriceDate = DateUtility.addDays( p_mostRecentPriceDate, 1 );
			YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, p_mostRecentPriceDate );
			missingHistory = yahooDataRequest.getPriceHistory();
		}

		return missingHistory;
	}
	
	public void initialisePriceHistory( String p_ticker, PoolableConnection p_connection ) throws SQLException {
			
		Date beginDate = new Date(0);
		YahooDataRequest yahooDataRequest = new YahooDataRequest( p_ticker, beginDate );
		List<DataFeedTO> missingHistory = yahooDataRequest.getPriceHistory();
		databaseProc.insertDataRequestHistory( p_ticker, DateUtility.getTodayDate(), p_connection );
		databaseProc.insertPriceHistory( p_ticker, missingHistory, p_connection );
		
	}
	
	public List<ListedStockTO> getListedStocks( PoolableConnection p_connection ) throws SQLException {
		
		List<ListedStockTO> listedStocks = new ArrayList<ListedStockTO>();		

		listedStocks = databaseProc.getListedStocks( p_connection );

		return listedStocks;
	}
	
	public List<ListedStockTO> getInitialListedStocks() {
		return InitialListedStocks.listedStocks;
	}

	public void updateWatchlist(  ListedStockTO p_stock, PoolableConnection p_connection ) throws SQLException {
		databaseProc.updateWatchlistedStocks( p_stock, p_connection );
	}

	public Date getMostRecentRequestDate( String p_ticker, PoolableConnection p_connection ) throws SQLException {

		Date mostRecentRequestDate = databaseProc.getMostRecentRequestDate( p_ticker, p_connection );
		if ( mostRecentRequestDate == null ) {
			mostRecentRequestDate = new Date(0);
		}

		return mostRecentRequestDate;
	}

	public Date getMostRecentPricehistoryDate( String p_ticker, PoolableConnection p_connection ) throws SQLException {
		Date mostRecentPriceDate = databaseProc.getMostRecentPricehistoryDate( p_ticker, p_connection );
		if ( mostRecentPriceDate == null ) {
			mostRecentPriceDate = new Date(0);
		}

		return mostRecentPriceDate;
	}

	public void insertPriceHistory( String p_ticker, List<DataFeedTO> p_history, PoolableConnection p_connection ) throws SQLException {
		databaseProc.insertPriceHistory( p_ticker, p_history, p_connection );
	}

	public void insertDataRequestHistory( String p_ticker, Date p_dateRequest, PoolableConnection p_connection ) throws SQLException {
		databaseProc.insertDataRequestHistory( p_ticker, p_dateRequest, p_connection );
	}
	
}
