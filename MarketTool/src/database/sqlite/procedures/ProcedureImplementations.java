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
import utility.DateUtility;

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
			System.out.println("most recent date " +p_ticker+ " : " + dateString);
		}
		if ( dateString != null ) {
			mostRecentPriceDate = DateUtility.parseStringToDate( dateString );
		}
		
		return mostRecentPriceDate;
	}
	
	public List<DataFeedTO> searchPriceHistory( String p_ticker, Date p_beginDate, Date p_endDate, Connection p_connection ) throws SQLException {
		
		List<DataFeedTO> priceHistory = new ArrayList<DataFeedTO>(400);
		
		PreparedStatement preparedStatement = p_connection.prepareStatement( ProcedureDefinitions.S_PRICEHISTORY );
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
}
