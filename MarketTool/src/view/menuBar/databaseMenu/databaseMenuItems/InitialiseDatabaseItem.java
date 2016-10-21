package view.menuBar.databaseMenu.databaseMenuItems;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import applicationConstants.InitialListedStocks;
import applicationConstants.StringConstants;
import database.ConnectionManager;
import database.DatabaseFacade;
import database.PoolableConnection;
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
		
		DatabaseFacade databaseFacade = new DatabaseFacade();
		databaseFacade.initialiseDatabase();
		
	}
	
}
