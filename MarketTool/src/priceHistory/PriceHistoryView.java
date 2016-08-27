package priceHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import applicationConstants.InitialListedStocks;
import applicationConstants.StringConstants;
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
import priceHistory.dataFeed.yahooFinance.YahooDataRequest;
import view.javaFXChart.DateAxis;

public class PriceHistoryView extends BorderPane {
	
	private ListView<ListedStockTO> stockListView;
	private LineChart<Date,Number> stockPriceChart;
	
	public PriceHistoryView() {
		super();
		
		createComponents();
		layoutComponents();
		
		populateComponents();
	}
	
	private void createComponents() {
		createStockListView();
		createPriceChart();
	}
	
	private void populateComponents() {
		ObservableList<ListedStockTO> observableList = FXCollections.observableArrayList( InitialListedStocks.listedStocks );
		populateStockListView( observableList );
		
	}
	
	private void layoutComponents() {
		super.setLeft( stockListView );
		super.setCenter( stockPriceChart );
	}
	
	private void createStockListView() {
		stockListView = new ListView<ListedStockTO>();
		stockListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				Series<Date, Number> closePriceSeries = getPriceChartData( newValue.getTicker() );
				populateStockPriceChart( closePriceSeries );
			}
		});
	}
	
	private void populateStockListView( ObservableList<ListedStockTO> p_observableList ) {
		
		stockListView.setItems( p_observableList );
	}
	
	private LineChart<Date,Number> createPriceChart() {
		final DateAxis xDateAxis = new DateAxis();
        final NumberAxis yPriceAxis = new NumberAxis();
        yPriceAxis.setForceZeroInRange(false);
        
        stockPriceChart = new LineChart(xDateAxis,yPriceAxis);
		
        stockPriceChart.setAnimated(false);
        stockPriceChart.setCreateSymbols(false);
		
		return stockPriceChart;
	}
	
	private void populateStockPriceChart( Series<Date, Number> p_stockPriceData ){
		
		stockPriceChart.getData().add(p_stockPriceData);
	}
	
	/**
	 * 
	 * @param p_ticker ticker of stock
	 * @return
	 */
	private XYChart.Series<Date, Number> getPriceChartData( String p_ticker ) {
		
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		
		DateFormat dateFormat = new SimpleDateFormat( StringConstants.DATE_FORMAT );
		
		Date l_mostRecentDate = new Date(0);
		Date l_thisDate;
		Date l_todayDate = Date.from( ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York")).toInstant() );
		
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
				
				preparedStatement.setString( 1, p_ticker );
				preparedStatement.setString( 2, "2011-02-02" );
				preparedStatement.setString( 3, "2012-08-03" );
				
				Instant start = Instant.now();
				
				ResultSet results = preparedStatement.executeQuery();
				Instant mid = Instant.now();
				System.out.println("Db query duration: " +  Duration.between(start, mid).getNano() );
				
				ArrayList<DataFeedTO> list = new ArrayList<DataFeedTO>();
				ObservableList<DataFeedTO> l_priceHistory = FXCollections.observableList( list );
				
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
					
					l_thisDate = dateFormat.parse(results.getString("DATE"));
					Data<Date, Number> data = new XYChart.Data( l_thisDate, (Number)results.getBigDecimal("CLOSEPRICE") );
					
					closePriceSeries.getData().add( data );
					
					if ( l_thisDate.after(l_mostRecentDate) ) {
						l_mostRecentDate = l_thisDate;
					}
				}
				
				Instant end = Instant.now();
				System.out.println("Data parse duration: " +  Duration.between(mid, end).getNano() );
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if ( l_todayDate.after(l_mostRecentDate) ) {
			YahooDataRequest l_rr = new YahooDataRequest( "IBM", l_mostRecentDate );
			ObservableList<DataFeedTO> l_priceHistory = l_rr.getPriceHistory();
		}
		
		return closePriceSeries;
	}

}
