package view.menuBar.databaseMenu.databaseMenuItems;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import applicationConstants.InitialListedStocks;
import applicationConstants.StringConstants;
import database.ConnectionManager;
import database.PoolableConnection;
import database.sqlite.Procs;
import database.sqlite.Tables;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import priceHistory.ListedStockTO;

public class InitialiseDatabaseItem extends MenuItem {
	
	public InitialiseDatabaseItem() {
		super( StringConstants.DATA_INITIALISEDB_TITLE );
		
		this.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				initialiseDatabase();
			}
		});
	}
	
	private void initialiseDatabase() {
		
		PoolableConnection connection = ConnectionManager.INSTANCE.getConnection();
		
		try {
			connection.setAutoCommit(false);
			
			createTables( connection );
			insertInitialData( connection );
			
			connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	private void createTables( Connection p_connection ) throws SQLException {
		Statement statement = p_connection.createStatement();
		
		statement.execute( Tables.DROP_LISTEDSTOCKS );
		statement.execute( Tables.CREATE_LISTEDSTOCKS );
		
		statement.execute( Tables.DROP_PRICEHISTORY );
		statement.execute( Tables.CREATE_PRICEHISTORY );
		
		statement.execute( Tables.DROP_DATAREQUESTHISTORY );
		statement.execute( Tables.CREATE_DATAREQUESTHISTORY );
	}
	
	private void insertInitialData( Connection p_connection ) throws SQLException {
		ArrayList<ListedStockTO> stocklist = InitialListedStocks.listedStocks;
		PreparedStatement prepstatement = p_connection.prepareStatement( Procs.I_LISTEDSTOCKS );
		for ( ListedStockTO stock : stocklist ) {
			prepstatement.setString( 1, stock.getTicker() );
			prepstatement.setString( 2, stock.getFullname() );
			prepstatement.addBatch();
		}
		int[] results = prepstatement.executeBatch();
	}
	
}
