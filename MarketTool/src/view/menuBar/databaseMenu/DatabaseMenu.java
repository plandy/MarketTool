package view.menuBar.databaseMenu;

import applicationConstants.StringConstants;
import javafx.scene.control.Menu;
import view.menuBar.databaseMenu.databaseMenuItems.ClearAllDataItem;
import view.menuBar.databaseMenu.databaseMenuItems.InitialiseDatabaseItem;

public class DatabaseMenu extends Menu {
	
	public DatabaseMenu() {
		super( StringConstants.DATA_MENU_TITLE );
		
		this.getItems().add( new InitialiseDatabaseItem() );
		this.getItems().add( new ClearAllDataItem() );
		
	}

}
