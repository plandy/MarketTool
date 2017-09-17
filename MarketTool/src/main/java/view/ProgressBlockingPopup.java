package view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ProgressBlockingPopup extends Stage {

    private final ProgressBar progressBar;

    public ProgressBlockingPopup( Window p_owner ) {

        super.initOwner( p_owner );
        super.initModality( Modality.APPLICATION_MODAL );

        progressBar = new ProgressBar(0);
        VBox dialogWindow = new VBox();
        dialogWindow.getChildren().add(progressBar);

        Scene dialogScene = new Scene(dialogWindow);

        super.setScene( dialogScene );

    }

    public void updateProgress( double p_progress ) {
        Platform.runLater(() -> {
            progressBar.setProgress( p_progress );
        });
    }

}
