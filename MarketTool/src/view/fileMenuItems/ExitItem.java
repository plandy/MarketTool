package view.fileMenuItems;

import applicationConstants.StringConstants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class ExitItem extends MenuItem {
	
	public ExitItem( String p_title ) {
		super( p_title, null );
		this.setOnAction( new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
			
		});
	}
	
	public ExitItem() {
		super( StringConstants.FILE_EXIT_TITLE );
		this.setOnAction( new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
			
		});
	}

}
