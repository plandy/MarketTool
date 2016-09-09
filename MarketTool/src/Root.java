import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import applicationConstants.InitialListedStocks;
import applicationConstants.StringConstants;
import database.ConnectionPool;
import database.PoolableConnection;
import database.sqlite.Procs;
import database.sqlite.Tables;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryView;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import utility.SystemInformationUtility;
import view.javaFXChart.DateAxis;
import view.menuBar.TopMenuBar;

public class Root extends Application {
	
	public static void main ( String[] args ) {
		launch( args );
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle( StringConstants.APPLICATION_TITLE );
		initializeApplicationWindow( primaryStage );
		
		final VBox root = new VBox();
		root.setAlignment(Pos.TOP_CENTER);
		Scene l_scene = new Scene(root);
	
		root.getChildren().addAll( new TopMenuBar( primaryStage ) );
		root.getChildren().add( new PriceHistoryView() );
		
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
	
	private void initialDBFunction() {
		ConnectionPool pool = new ConnectionPool(1,1);
		PoolableConnection poolableConnection = null;
		try {
			poolableConnection = pool.requestConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( poolableConnection != null ) {
			initialiseDatabase( poolableConnection );
		}
	}
	
	private void initialiseDatabase( PoolableConnection p_connection ) {
		
		try {
			p_connection.setAutoCommit(false);
			
			createTables( p_connection );
			insertInitialData( p_connection );
			
			p_connection.commit();
		} catch (SQLException e) {
			p_connection.silentRollback();
		}
	}
	
	private void createTables( Connection p_connection ) throws SQLException {
		Statement statement = p_connection.createStatement();
		
		statement.execute( Tables.DROP_LISTEDSTOCKS );
		statement.execute( Tables.CREATE_LISTEDSTOCKS );
		
		statement.execute( Tables.DROP_PRICEHISTORY );
		statement.execute( Tables.CREATE_PRICEHISTORY );
		
		statement.execute( Tables.DROP_DATAREQUESTHISTORY );
		statement.execute( Tables.CREATE_DATAREQUESTHISTORY );
	}
	
	private void insertInitialData( Connection p_connection ) throws SQLException {
		ArrayList<ListedStockTO> stocklist = InitialListedStocks.listedStocks;
		PreparedStatement prepstatement = p_connection.prepareStatement(Procs.I_LISTEDSTOCKS);
		for ( ListedStockTO stock : stocklist ) {
			prepstatement.setString(1, stock.getTicker());
			prepstatement.setString(2, stock.getFullname());
			prepstatement.addBatch();
		}
		int[] results = prepstatement.executeBatch();
	}
	
}
