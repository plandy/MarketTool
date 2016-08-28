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
	
	private void getInsert() {
		ConnectionPool pool = new ConnectionPool(1,1);
		Connection connection = null;
		try {
			connection = pool.requestConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( connection != null ) {
			YahooDataRequest l_rr = new YahooDataRequest( "IBM" );
			ObservableList<DataFeedTO> priceHistory = l_rr.getPriceHistory();
			
			try {
				connection.setAutoCommit(false);
				
				Instant start = Instant.now();
				
				Statement statement = connection.createStatement();
				
				statement.execute( Tables.DROP_PRICEHISTORY );
				statement.execute( Tables.CREATE_PRICEHISTORY );
				
				PreparedStatement preparedStatement = connection.prepareStatement( Procs.I_PRICEHISTORY );
				
				for ( DataFeedTO dataTO : priceHistory ) {
					
					//preparedStatement.setDate(1, new java.sql.Date(dataTO.getDate().getTime()) );
					preparedStatement.setString(1, "IBM" );
					preparedStatement.setString(2, dataTO.getDate() );
					preparedStatement.setBigDecimal(3, dataTO.getOpenPrice());
					preparedStatement.setBigDecimal(4, dataTO.getHighPrice());
					preparedStatement.setBigDecimal(5, dataTO.getLowPrice());
					preparedStatement.setBigDecimal(6, dataTO.getClosePrice());
					preparedStatement.setInt(7, dataTO.getVolume());
					
					preparedStatement.addBatch();
					
				}
				
				int[] results = preparedStatement.executeBatch();
				
				Instant end = Instant.now();
				System.out.println("Data insert duration: " + Duration.between(start, end).getNano());
				//System.out.println("Number of rows inserted : " + results[0]);
				
				connection.commit();
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void initializeApplicationWindow( Stage primaryStage ) {
		
		//set window to the size of the currently focused monitor
		int l_screenWidth = SystemInformationUtility.getScreenWidth();
		int l_screenHeight = SystemInformationUtility.getScreenHeight();
		primaryStage.setWidth( l_screenWidth );
		primaryStage.setHeight( l_screenHeight );
		
	}
	
	private void initialiseDatabase( PoolableConnection p_connection ) {
		
		try {
			p_connection.setAutoCommit(false);
			
			createTables( p_connection );
			insertInitialData( p_connection );
			
			p_connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException("failed to initialise database");
		} finally {
			p_connection.silentRollback();
		}
	}
	
	private void createTables( Connection p_connection ) throws SQLException {
		Statement statement = p_connection.createStatement();
		
		statement.execute( Tables.DROP_LISTEDSTOCKS );
		statement.execute( Tables.CREATE_LISTEDSTOCKS );
		
		statement.execute( Tables.DROP_PRICEHISTORY );
		statement.execute( Tables.CREATE_PRICEHISTORY );
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
