package priceHistory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import applicationConstants.InitialListedStocks;
import database.ConnectionPool;
import database.sqlite.Procs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import priceHistory.dataFeed.DataFeedTO;
import view.javaFXChart.DateAxis;

public class PriceHistoryView extends BorderPane {
	
	private ListView<ListedStockTO> stockListView;
	private LineChart<Date,Number> stockPriceChart;
	
	public PriceHistoryView() {
		super();
		
		stockListView = new ListView<ListedStockTO>();
		stockListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				
			}
		});
		super.setLeft( stockListView );
		stockPriceChart = createPriceChart();
		super.setCenter( stockPriceChart );
		
		ObservableList<ListedStockTO> observableList = FXCollections.observableArrayList(InitialListedStocks.listedStocks);
		populateStockListView( observableList );
		
		Series<Date, Number> closePriceSeries = getPriceChartData();
		populateStockPriceChart( closePriceSeries );
	}
	
	private void populateStockListView( ObservableList<ListedStockTO> p_observableList ) {
		
		stockListView.setItems( p_observableList );
	}
	
	private LineChart<Date,Number> createPriceChart() {
		final DateAxis xDateAxis = new DateAxis();
        final NumberAxis yPriceAxis = new NumberAxis();
        yPriceAxis.setForceZeroInRange(false);
		final LineChart<Date,Number> lineChart = new LineChart(xDateAxis,yPriceAxis);
		
		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(false);
		
		return lineChart;
	}
	
	private void populateStockPriceChart( Series<Date, Number> p_stockPriceData ){
		
		stockPriceChart.getData().add(p_stockPriceData);
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

}
