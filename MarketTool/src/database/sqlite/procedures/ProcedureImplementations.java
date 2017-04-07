package database.sqlite.procedures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import priceHistory.ListedStockTO;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;
import utility.DateUtility;
import utility.logger.Log;

public class ProcedureImplementations {
	
	public Date getMostRecentRequestDate( String p_ticker, Connection p_connection ) throws SQLException {
		
		Date mostRecentDate = null;
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.GET_MOSTRECENT_DATAREQUEST_DATE );
		preparedStatement.setString( 1, p_ticker );
		
		ResultSet results = preparedStatement.executeQuery();
		
		while ( results.next() ) {
			if ( results.getString("REQUESTDATE") != null ) {
				mostRecentDate = DateUtility.parseStringToDate( results.getString("REQUESTDATE") );
			}
		}
		if ( mostRecentDate == null ) {
			mostRecentDate = new Date(0);
		}
		
		return mostRecentDate;
	}
	
	public Date getMostRecentPricehistoryDate( String p_ticker, Connection p_connection ) throws SQLException {
		
		Date mostRecentPriceDate = null;
		String dateString = null;
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.GET_MOSTRECENT_PRICEHISTORY_DATE );
		preparedStatement.setString( 1, p_ticker );
		
		ResultSet results = preparedStatement.executeQuery();
		
		while ( results.next() ) {
			dateString = results.getString("DATE");
			Log.info( "most recent date " +p_ticker+ " : " + dateString );
		}
		if ( dateString != null ) {
			mostRecentPriceDate = DateUtility.parseStringToDate( dateString );
		}
		
		return mostRecentPriceDate;
	}
	
	public PriceHistoryTO searchPriceHistory( String p_ticker, Date p_beginDate, Date p_endDate, Connection p_connection ) throws SQLException {
		
		//List<DataFeedTO> priceHistory = new ArrayList<DataFeedTO>(400);
		PriceHistoryTO priceHistory = new PriceHistoryTO();
		
		int size = 0;
		
		PreparedStatement preparedStatement_count = p_connection.prepareStatement(  ProcedureDefinitions.S_PRICEHISTORY_COUNT );
		preparedStatement_count.setString( 1, p_ticker );
		preparedStatement_count.setString( 2, DateUtility.parseDateToString(p_beginDate) );
		preparedStatement_count.setString( 3, DateUtility.parseDateToString(p_endDate) );
		
		ResultSet results_count = preparedStatement_count.executeQuery();
		while ( results_count.next() ) {
			size = results_count.getInt( "COUNT" );
		}
		priceHistory.initialiseArrays( size );
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.S_PRICEHISTORY );
		preparedStatement.setString( 1, p_ticker );
		preparedStatement.setString( 2, DateUtility.parseDateToString(p_beginDate) );
		preparedStatement.setString( 3, DateUtility.parseDateToString(p_endDate) );
		
//		while ( results.next() ) {
//			DataFeedTO dataTO = new DataFeedTO();
//			
//			dataTO.setTicker( results.getString("TICKER") );
//			dataTO.setDate( results.getString("DATE") );
//			dataTO.setOpenPrice( results.getBigDecimal("OPENPRICE") );
//			dataTO.setHighPrice( results.getBigDecimal("HIGHPRICE") );
//			dataTO.setLowPrice( results.getBigDecimal("LOWPRICE") );
//			dataTO.setClosePrice( results.getBigDecimal("CLOSEPRICE") );
//			dataTO.setVolume( results.getInt("VOLUME") );
//			
//			priceHistory.add( dataTO );
//		}
		int index = 0;
		ResultSet results = preparedStatement.executeQuery();
		
		while ( results.next() ) {
			priceHistory.date[index] = results.getString("DATE");
			priceHistory.openPrice[index] = results.getDouble("OPENPRICE");
			priceHistory.highPrice[index] = results.getDouble("HIGHPRICE");
			priceHistory.lowPrice[index] = results.getDouble("LOWPRICE");
			priceHistory.closePrice[index] = results.getDouble("CLOSEPRICE");
			priceHistory.volume[index] = results.getInt("VOLUME");
			
			index++;
		}
		
		return priceHistory;
	}
	
	public void insertDataRequestHistory( String p_ticker, Date p_date, Connection p_connection ) throws SQLException {
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.I_DATAREQUESTHISTORY );
		
		preparedStatement.setString(1, p_ticker );
		preparedStatement.setString(2, DateUtility.parseDateToString(p_date) );
		
		preparedStatement.addBatch();
		
		int[] results = preparedStatement.executeBatch();
	}
	
	public void insertPriceHistory( String p_ticker, List<DataFeedTO> p_priceHistory, Connection p_connection ) throws SQLException {
		
		if ( p_priceHistory.isEmpty() ) {
			return;
		}
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.I_PRICEHISTORY );
		
		for ( DataFeedTO dataTO : p_priceHistory ) {
			
			preparedStatement.setString(1, p_ticker );
			preparedStatement.setString(2, dataTO.getDateAsString() );
			preparedStatement.setBigDecimal(3, dataTO.getOpenPrice());
			preparedStatement.setBigDecimal(4, dataTO.getHighPrice());
			preparedStatement.setBigDecimal(5, dataTO.getLowPrice());
			preparedStatement.setBigDecimal(6, dataTO.getClosePrice());
			preparedStatement.setInt(7, dataTO.getVolume());
			
			preparedStatement.addBatch();
			
		}
		
		int[] results = preparedStatement.executeBatch();
	}
	
	public List<ListedStockTO> getListedStocks( Connection p_connection ) throws SQLException {
		
		List<ListedStockTO> listedStocks = new ArrayList<ListedStockTO>(40);
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.GET_ALL_LISTEDSTOCKS );
		ResultSet results = preparedStatement.executeQuery();
		
		while ( results.next() ) {
			ListedStockTO stockTO = new ListedStockTO( results.getString("TICKER"), results.getString("FULLNAME") );
			listedStocks.add( stockTO );
		}
		
		return listedStocks;
	}

	public boolean isTableExists( String p_tableName, Connection p_connection ) throws SQLException {
		boolean isExists = false;

		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.IS_TABLE_EXISTS );

		preparedStatement.setString(1, p_tableName );

		ResultSet result = preparedStatement.executeQuery();
		while ( result.next() ) {
			isExists = result.getBoolean(1);
		}

		return isExists;
	}
}
