package view.menuBar.dataFeedMenu;

import applicationConstants.StringConstants;
import javafx.scene.control.Menu;
import view.menuBar.dataFeedMenu.dataFeedMenuItems.BackgroundDataRequestItem;

public class DataFeedMenu extends Menu {
	
	public DataFeedMenu() {
		super( StringConstants.DATAFEED_MENU_TITLE );
		
		this.getItems().add( new BackgroundDataRequestItem() );
	}
	
}
