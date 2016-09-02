package priceHistory;

import java.util.Date;

import applicationConstants.InitialListedStocks;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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
	private BarChart<String,Number> stockVolumeChart;
	
	private BorderPane chartAreaPane;
	
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
		createChartAreaPane();
		createPriceChart();
		createStockVolumeChart();
	}
	
	private void populateComponents() {
		ObservableList<ListedStockTO> observableList = FXCollections.observableArrayList( InitialListedStocks.listedStocks );
		populateStockListView( observableList );
	}
	
	private void layoutComponents() {
		super.setLeft( stockListView );
		super.setCenter( chartAreaPane );
		chartAreaPane.setCenter( stockPriceChart );
		chartAreaPane.setBottom( stockVolumeChart );
	}
	
	private void createChartAreaPane() {
		chartAreaPane = new BorderPane();
	}
	
	private void createStockListView() {
		stockListView = new ListView<ListedStockTO>();
		stockListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				populateStockPriceChart( newValue.getTicker() );
				populateStockVolumeChart( newValue.getTicker() );
			}
		});
	}
	
	private void populateStockListView( ObservableList<ListedStockTO> p_observableList ) {
		stockListView.setItems( p_observableList );
	}
	
	private void createPriceChart() {
		final DateAxis xDateAxis = new DateAxis();
        final NumberAxis yPriceAxis = new NumberAxis();
        yPriceAxis.setForceZeroInRange(false);
        
        stockPriceChart = new LineChart<Date, Number>( xDateAxis, yPriceAxis );
		
        stockPriceChart.setAnimated(false);
        stockPriceChart.setLegendVisible(false);
        stockPriceChart.setCreateSymbols(false);
        stockPriceChart.setHorizontalGridLinesVisible(true);
        stockPriceChart.setVerticalGridLinesVisible(true);
	}
	
	private void populateStockPriceChart( String p_ticker ){
		XYChart.Series<Date, Number> closePriceSeries = controller.getPriceChartData( p_ticker );
		stockPriceChart.getData().add( closePriceSeries );
	}
	
	private void createStockVolumeChart() {
		final CategoryAxis xCategoryAxis = new CategoryAxis();
        final NumberAxis yPriceAxis = new NumberAxis();
        xCategoryAxis.setTickLabelsVisible(false);
        xCategoryAxis.setTickMarkVisible(false);
        yPriceAxis.setForceZeroInRange(false);
        yPriceAxis.setTickLabelsVisible(true);
        yPriceAxis.setTickMarkVisible(true);
        yPriceAxis.setMinorTickVisible(false);
        
        stockVolumeChart = new BarChart<String, Number>( xCategoryAxis, yPriceAxis );
		
        stockVolumeChart.setAnimated(false);
        stockVolumeChart.setLegendVisible(false);
        stockVolumeChart.setHorizontalGridLinesVisible(false);
        stockVolumeChart.setVerticalGridLinesVisible(false);
	}
	
	private void populateStockVolumeChart( String p_ticker ) {
		XYChart.Series<String, Number> closePriceSeries = controller.getVolumeChartData( p_ticker );
		stockVolumeChart.getData().add( closePriceSeries );
	}

}
