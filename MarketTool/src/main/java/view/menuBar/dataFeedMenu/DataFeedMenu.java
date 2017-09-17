package view.menuBar.dataFeedMenu;

import applicationConstants.StringConstants;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import view.menuBar.dataFeedMenu.dataFeedMenuItems.BackgroundDataRequestItem;
import view.menuBar.dataFeedMenu.dataFeedMenuItems.BulkDataCollectItem;

public class DataFeedMenu extends Menu {
	
	public DataFeedMenu( MenuBar p_parentMenuBar ) {
		super( StringConstants.DATAFEED_MENU_TITLE );
		
		this.getItems().add( new BackgroundDataRequestItem() );
		this.getItems().add( new BulkDataCollectItem( p_parentMenuBar ) );
	}
	
}
