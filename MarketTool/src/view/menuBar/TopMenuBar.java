package view.menuBar;

import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import view.menuBar.databaseMenu.DatabaseMenu;
import view.menuBar.fileMenu.FileMenu;

public class TopMenuBar extends MenuBar {
	
	public TopMenuBar ( Stage p_primaryStage ) {
		
		this.getMenus().add( new FileMenu() );
		this.getMenus().add( new DatabaseMenu() );
		
	}
}
