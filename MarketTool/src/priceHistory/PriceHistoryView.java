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
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import view.javaFXChart.DateAxis;

public class PriceHistoryView extends BorderPane {
	
	private ListView<ListedStockTO> stockListView;
	
	private GridPane chartAreaPane;
	private LineChart<Date,Number> stockPriceChart;
	private BarChart<String,Number> stockVolumeChart;
	
	private PriceHistoryController controller;
	
	public PriceHistoryView() {
		super();
		controller = new PriceHistoryController();
		
		createComponents();
		layoutComponents();
		
		initialiseComponents();
		
		addListeners();
	}
	
	private void addListeners() {
		addStockListListeners();
	}
	
	private void createComponents() {
		createStockListView();
		createChartAreaPane();
		createPriceChart();
		createStockVolumeChart();
	}
	
	private void initialiseComponents() {
		ObservableList<ListedStockTO> observableList = FXCollections.observableArrayList( InitialListedStocks.listedStocks );
		populateStockListView( observableList );
	}
	
	private void layoutComponents() {
		super.setLeft( stockListView );
		super.setCenter( chartAreaPane );
		
		chartAreaPane.add(stockPriceChart, 0, 0);
		chartAreaPane.add(stockVolumeChart, 0, 1);
		layoutChartAreaPane();
	}
	
	private void addStockListListeners() {
		stockListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				populateStockPriceChart( newValue.getTicker() );
				populateStockVolumeChart( newValue.getTicker() );
			}
		});
	}
	
	private void createChartAreaPane() {
		chartAreaPane = new GridPane();
		chartAreaPane.setGridLinesVisible( false );
	}
	
	private void layoutChartAreaPane() {
		ColumnConstraints col0Constraint = new ColumnConstraints();
		col0Constraint.setHgrow(Priority.ALWAYS);
		chartAreaPane.getColumnConstraints().add(0, col0Constraint);
		
		RowConstraints row0Constraint = new RowConstraints();
		row0Constraint.setPercentHeight(70);
		chartAreaPane.getRowConstraints().add(0, row0Constraint);
		
		RowConstraints row1Constraint = new RowConstraints();
		row1Constraint.setPercentHeight(30);
		chartAreaPane.getRowConstraints().add(1, row1Constraint);
	}
	
	private void createStockListView() {
		stockListView = new ListView<ListedStockTO>();
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
