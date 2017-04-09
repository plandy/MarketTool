package view.menuBar.dataFeedMenu.dataFeedMenuItems;

import applicationConstants.StringConstants;
import javafx.scene.control.CheckMenuItem;
import priceHistory.dataFeed.backgroundService.BackgroundDataFeedService;

public class BackgroundDataRequestItem extends CheckMenuItem {
	
	private BackgroundDataFeedService backgroundDataService;
	
	public BackgroundDataRequestItem() {
		super( StringConstants.DATAFEED_BACKGROUNDREQUESTS_TITLE );
		
		backgroundDataService = new BackgroundDataFeedService();
		this.setOnAction( e -> {
			if ( this.isSelected() ) {
				backgroundDataService.start();
			} else {
				backgroundDataService.stop();
			}
				
		});
	}
	
}
