package view.menuBar.databaseMenu.databaseMenuItems;

import applicationConstants.StringConstants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class ClearAllDataItem extends MenuItem {
	public ClearAllDataItem() {
		super( StringConstants.DATA_CLEARALLDATA_TITLE );
		
		this.setOnAction( new EventHandler<ActionEvent>() {
			
			@Override
			public void handle ( ActionEvent event ) {
				
			}
		
		});
	}

}
