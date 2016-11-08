package view.menuBar.databaseMenu.databaseMenuItems;

import applicationConstants.StringConstants;
import database.DatabaseFacade;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

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
