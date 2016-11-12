package view.menuBar;

import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import view.menuBar.dataFeedMenu.DataFeedMenu;
import view.menuBar.databaseMenu.DatabaseMenu;
import view.menuBar.fileMenu.FileMenu;

public class TopMenuBar extends MenuBar {
	
	public TopMenuBar () {
		
		this.getMenus().add( new FileMenu() );
		this.getMenus().add( new DatabaseMenu() );
		this.getMenus().add( new DataFeedMenu() );
	}
}
