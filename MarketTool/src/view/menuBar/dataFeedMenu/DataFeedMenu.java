package view.menuBar.dataFeedMenu;

import applicationConstants.StringConstants;
import javafx.scene.control.Menu;
import view.menuBar.dataFeedMenu.dataFeedMenuItems.BackgroundDataRequestItem;
import view.menuBar.dataFeedMenu.dataFeedMenuItems.BulkDataCollectItem;

public class DataFeedMenu extends Menu {
	
	public DataFeedMenu() {
		super( StringConstants.DATAFEED_MENU_TITLE );
		
		this.getItems().add( new BackgroundDataRequestItem() );
		this.getItems().add( new BulkDataCollectItem() );
	}
	
}
