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

import applicationConstants.StringConstants;
import database.ConnectionPool;
import database.sqlite.Procs;
import database.sqlite.Tables;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
		
		TabPane tabPane = new TabPane();
		
		final VBox root = new VBox();
		root.setAlignment(Pos.TOP_CENTER);
		Scene l_scene = new Scene(root);
	
		root.getChildren().addAll( new TopMenuBar( primaryStage ), tabPane );
		
		final DateAxis xDateAxis = new DateAxis();
        final NumberAxis yPriceAxis = new NumberAxis();
        yPriceAxis.setForceZeroInRange(false);
		final LineChart<Date,Number> lineChart = new LineChart(xDateAxis,yPriceAxis);
		
		Series<Date, Number> closePriceSeries = getPriceChartData();
		lineChart.getData().add(closePriceSeries);
		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(false);
		
		root.getChildren().add(lineChart);
		
		primaryStage.setScene( l_scene );
		primaryStage.setMaximized(true);
		primaryStage.show();
		
//		primaryStage.setOnCloseRequest(new windowCloseEvent());

		
	}
	
	private XYChart.Series<Date, Number> getPriceChartData() {
		
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		
		ConnectionPool pool = new ConnectionPool(1,1);
		Connection connection = null;
		try {
			connection = pool.requestConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( connection != null ) {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement( Procs.S_PRICEHISTORY );
				
				preparedStatement.setString( 1, "IBM" );
				preparedStatement.setString( 2, "2011-02-02" );
				preparedStatement.setString( 3, "2012-08-03" );
				
				Instant start = Instant.now();
				
				ResultSet results = preparedStatement.executeQuery();
				Instant mid = Instant.now();
				System.out.println("Db query duration: " +  Duration.between(start, mid).getNano() );
				
				ArrayList<DataFeedTO> list = new ArrayList<DataFeedTO>();
				ObservableList<DataFeedTO> l_priceHistory = FXCollections.observableList( list );
				
				DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
				
				while ( results.next() ) {
					DataFeedTO dataTO = new DataFeedTO();
					
					dataTO.setTicker( results.getString("TICKER") );
					dataTO.setDate( results.getString("DATE") );
					dataTO.setOpenPrice( results.getBigDecimal("OPENPRICE") );
					dataTO.setHighPrice( results.getBigDecimal("HIGHPRICE") );
					dataTO.setLowPrice( results.getBigDecimal("LOWPRICE") );
					dataTO.setClosePrice( results.getBigDecimal("CLOSEPRICE") );
					dataTO.setVolume( results.getInt("VOLUME") );
					
//					dataTO.setTicker( results.getString(1) );
//					dataTO.setDate( results.getString(2) );
//					dataTO.setOpenPrice( results.getBigDecimal(3) );
//					dataTO.setHighPrice( results.getBigDecimal(4) );
//					dataTO.setLowPrice( results.getBigDecimal(5) );
//					dataTO.setClosePrice( results.getBigDecimal(6) );
//					dataTO.setVolume( results.getInt(7) );
					
					Data<Date, Number> data = new XYChart.Data( dateFormat.parse(results.getString("DATE")), (Number)results.getBigDecimal("CLOSEPRICE") );
					
					closePriceSeries.getData().add( data );
				}
				
				Instant end = Instant.now();
				System.out.println("Data parse duration: " +  Duration.between(mid, end).getNano() );		
								
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return closePriceSeries;
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
	
	
	
}
