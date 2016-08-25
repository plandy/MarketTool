package view.menuBar.fileMenu;

import applicationConstants.StringConstants;
import javafx.scene.control.Menu;
import view.menuBar.fileMenu.fileMenuItems.ExitItem;

public class FileMenu extends Menu {
	
	public FileMenu() {
		super( StringConstants.FILE_MENU_TITLE );
		
		this.getItems().add( new ExitItem() );
	}
	
}
