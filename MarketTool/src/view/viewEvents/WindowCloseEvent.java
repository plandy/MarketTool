package view.viewEvents;

import java.util.Optional;

import applicationConstants.StringConstants;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.WindowEvent;

public class WindowCloseEvent implements EventHandler<WindowEvent> {

	@Override
	public void handle(WindowEvent event) {
		Alert l_alert = new Alert( AlertType.CONFIRMATION, StringConstants.WINDOW_CLOSE_DIALOG );
		Optional<ButtonType> l_response = l_alert.showAndWait();
		if ( l_response.isPresent() && l_response.get().equals( ButtonType.OK ) ) {
			Platform.exit();
		} else {
			event.consume();
		}
	}

}
