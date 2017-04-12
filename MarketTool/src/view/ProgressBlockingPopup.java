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

    public void updateProgress( int p_progress, int p_totalJobs) {
        Platform.runLater(() -> {
            if ( p_progress > 0 ) {
                progressBar.setProgress( 100 * ( 1 - (p_progress / p_totalJobs) ) );
            } else {
                progressBar.setProgress( 100 );
            }
        });
    }

}
