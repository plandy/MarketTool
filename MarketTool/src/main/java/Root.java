import applicationConstants.StringConstants;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utility.SystemInformationUtility;
import view.menuBar.TopMenuBar;
import view.priceHistory.PriceHistoryMainView;
import database.DatabaseFacade;

public class Root extends Application {
	
	public static void main ( String[] args ) {
		launch( args );
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		initializeDatabase();
		
		primaryStage.setTitle( StringConstants.APPLICATION_TITLE );
		initializeApplicationWindow( primaryStage );
		
		final VBox root = new VBox();
		root.setAlignment(Pos.TOP_CENTER);
		Scene l_scene = new Scene(root);
	
		root.getChildren().addAll( new TopMenuBar() );
		root.getChildren().add( new PriceHistoryMainView() );
		
		primaryStage.setScene( l_scene );
		primaryStage.setMaximized(true);
		primaryStage.show();
		
//		primaryStage.setOnCloseRequest(new windowCloseEvent());
		
	}
	
	private void initializeApplicationWindow( Stage primaryStage ) {
		
		//set window to the size of the currently focused monitor
		int l_screenWidth = SystemInformationUtility.getScreenWidth();
		int l_screenHeight = SystemInformationUtility.getScreenHeight();
		primaryStage.setWidth( l_screenWidth );
		primaryStage.setHeight( l_screenHeight );
		
	}

	private void initializeDatabase() {

		DatabaseFacade dbFacade = new DatabaseFacade();

		if ( dbFacade.isDatabaseInitialized() == false ) {
			dbFacade.initialiseDatabase();
		}

		if ( dbFacade.isDatabaseInitialized() == false ) {
			throw new RuntimeException( "Unable to initialize database" );
		}
	}


	
}
