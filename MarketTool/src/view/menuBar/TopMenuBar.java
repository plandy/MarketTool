package view.menuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import view.menuBar.fileMenu.FileMenu;

public class TopMenuBar extends MenuBar {
	
	public TopMenuBar ( Stage p_primaryStage ) {
		final Menu l_fileMenu = new FileMenu();
		this.getMenus().addAll( l_fileMenu );
		
	}
}
