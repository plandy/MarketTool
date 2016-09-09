package priceHistory;

import java.util.Date;

import applicationConstants.InitialListedStocks;
import applicationConstants.StringConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import view.javaFXChart.DateAxis;

public class PriceHistoryView extends BorderPane {
	
	private TabPane stockListTabPane;
	private ListView<ListedStockTO> stockListView;
	private ListView<ListedStockTO> stockWatchlistListView;
	
	private GridPane chartAreaPane;
	private LineChart<Date,Number> stockPriceChart;
	private BarChart<String,Number> stockVolumeChart;
	
	private PriceHistoryController controller;
	
	public PriceHistoryView() {
		super();
		controller = new PriceHistoryController( this );
		
		createComponents();
		layoutComponents();
		
		initialiseComponents();
	}
	
	private void createComponents() {
		createStockListTabPane();
		createChartAreaPane();
	}
	
	private void initialiseComponents() {
		ObservableList<ListedStockTO> observableList = controller.getListedStocks();
		populateStockListView( observableList );
	}
	
	private void layoutComponents() {
		super.setLeft( stockListTabPane );
		super.setCenter( chartAreaPane );
		
		layoutStockListTabPane();
		layoutChartAreaPane();
	}
	
	private void addStockListListeners() {
				
		stockListView.setCellFactory( factory -> {
			ListCell<ListedStockTO> cell = new ListCell();
			
			cell.textProperty().bind(cell.itemProperty().asString());
			
			ContextMenu contextMenu = new ContextMenu();
			MenuItem addToWatchlistItem = new MenuItem();
			addToWatchlistItem.setText( StringConstants.PRICEHISTORYVIEW_ADDTOWATCHLIST );
			addToWatchlistItem.setOnAction( event -> stockWatchlistListView.getItems().add(cell.getItem()) );
			contextMenu.getItems().add( addToWatchlistItem );
			
			cell.emptyProperty().addListener( (observableValue, oldIsEmpty, newIsEmpty) -> {
                if ( newIsEmpty ) {
                	cell.setContextMenu( null );
                } else {
                    cell.setContextMenu( contextMenu );
                }
            });
			
			cell.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});
			
			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});
			
			return cell;
		});
		
		stockListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				if ( newValue != null ) {
					controller.selectStock( newValue.getTicker() );
				}
			}
		});		

	}
	
	private void addStockWatchListListeners() {
		stockWatchlistListView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<ListedStockTO>() {
			@Override
			public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
				if ( newValue != null ) {
					controller.selectStock( newValue.getTicker() );
				}
			}
		});	
	}
	
	private void createStockListTabPane() {
		stockListTabPane = new TabPane();
		
		stockListTabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
			if ( oldTab != null ) {
				if ( oldTab.getContent() instanceof ListView ) {
					ListView listView = (ListView)oldTab.getContent();
					listView.getSelectionModel().clearSelection();
				}
			}
		});
		
		createStockListView();
		createStockWatchlistListView();
	}
	
	private void layoutStockListTabPane(){
		Tab stockListTab = new Tab();
		stockListTab.setContent( stockListView );
		stockListTab.setText( StringConstants.PRICEHISTORYVIEW_ALLSTOCKS_TAB );
		stockListTab.setClosable(false);
		stockListTabPane.getTabs().add( stockListTab );
		
		Tab stockWatchlistTab = new Tab();
		stockWatchlistTab.setContent( stockWatchlistListView );
		stockWatchlistTab.setText( StringConstants.PRICEHISTORYVIEW_WATCHLIST_TAB );
		stockWatchlistTab.setClosable(false);
		stockListTabPane.getTabs().add( stockWatchlistTab );
		
		stockListTabPane.getSelectionModel().select( stockListTab );
	}
	
	private void createChartAreaPane() {
		chartAreaPane = new GridPane();
		chartAreaPane.setGridLinesVisible( false );
		
		createPriceChart();
		createStockVolumeChart();
	}
	
	private void layoutChartAreaPane() {
		chartAreaPane.add( stockPriceChart, 0, 0 );
		chartAreaPane.add( stockVolumeChart, 0, 1 );
		
		ColumnConstraints col0Constraint = new ColumnConstraints();
		col0Constraint.setHgrow(Priority.ALWAYS);
		chartAreaPane.getColumnConstraints().add( 0, col0Constraint );
		
		RowConstraints row0Constraint = new RowConstraints();
		row0Constraint.setPercentHeight(70);
		chartAreaPane.getRowConstraints().add( 0, row0Constraint );
		
		RowConstraints row1Constraint = new RowConstraints();
		row1Constraint.setPercentHeight(30);
		chartAreaPane.getRowConstraints().add( 1, row1Constraint );
	}
	
	private void createStockListView() {
		stockListView = new ListView<ListedStockTO>();
		addStockListListeners();
	}
	
	private void createStockWatchlistListView() {
		stockWatchlistListView = new ListView<ListedStockTO>();
		addStockWatchListListeners();
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
	
	public void populateStockPriceChart( XYChart.Series<Date, Number> p_closePriceSeries ){
		stockPriceChart.getData().clear();
		stockPriceChart.getData().add( p_closePriceSeries );
	}
	
	public void populateStockVolumeChart( XYChart.Series<String, Number> p_closePriceSeries ) {
		stockVolumeChart.getData().clear();
		stockVolumeChart.getData().add( p_closePriceSeries );
	}

}
