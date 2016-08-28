package priceHistory;

import java.util.Date;

import applicationConstants.InitialListedStocks;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import view.javaFXChart.DateAxis;

public class PriceHistoryView extends BorderPane {
	
	private ListView<ListedStockTO> stockListView;
	private LineChart<Date,Number> stockPriceChart;
	
	private PriceHistoryController controller;
	
	public PriceHistoryView() {
		super();
		controller = new PriceHistoryController();
		
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
	
	private XYChart.Series<Date, Number> getPriceChartData( String p_ticker  ) {
		
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		closePriceSeries = controller.getPriceChartData( p_ticker );
		
		return closePriceSeries;
	}

}
