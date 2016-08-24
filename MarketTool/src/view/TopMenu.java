package view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import view.fileMenuItems.ExitItem;

public class TopMenu extends MenuBar {
	
	public TopMenu ( Stage p_primaryStage ) {
		final Menu l_menu1 = new Menu( "File" );
		l_menu1.getItems().add( new ExitItem() );
		this.getMenus().addAll( l_menu1 );
		
	}
}
