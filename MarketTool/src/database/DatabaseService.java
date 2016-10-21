package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import applicationConstants.InitialListedStocks;
import database.sqlite.Tables;
import database.sqlite.procedures.ProcedureDefinitions;
import priceHistory.ListedStockTO;

public class DatabaseService {
	
	public void createTables( Connection p_connection ) throws SQLException {
		Statement statement = p_connection.createStatement();
		
		statement.execute( Tables.DROP_LISTEDSTOCKS );
		statement.execute( Tables.CREATE_LISTEDSTOCKS );
		
		statement.execute( Tables.DROP_PRICEHISTORY );
		statement.execute( Tables.CREATE_PRICEHISTORY );
		
		statement.execute( Tables.DROP_DATAREQUESTHISTORY );
		statement.execute( Tables.CREATE_DATAREQUESTHISTORY );
	}
	
	public void insertInitialData( Connection p_connection ) throws SQLException {
		ArrayList<ListedStockTO> stocklist = InitialListedStocks.listedStocks;
		PreparedStatement prepstatement = p_connection.prepareStatement( ProcedureDefinitions.I_LISTEDSTOCKS );
		for ( ListedStockTO stock : stocklist ) {
			prepstatement.setString( 1, stock.getTicker() );
			prepstatement.setString( 2, stock.getFullname() );
			prepstatement.addBatch();
		}
		int[] results = prepstatement.executeBatch();
	}
	
}
