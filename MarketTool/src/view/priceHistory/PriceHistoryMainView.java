package view.priceHistory;

import java.util.ArrayList;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryController;
import view.BaseMainView;

public class PriceHistoryMainView extends BaseMainView {
	
	private StocklistView stocklistView;
	
	private BorderPane vbox;
	public ListView<CheckBox> checkBoxListView;
	
	private GridPane chartAreaPane;
	private StockPriceChart stockPriceChart;
	private StockVolumeChart stockVolumeChart;
	
	private PriceHistoryController controller;
	
	public PriceHistoryMainView() {
		super();
		controller = new PriceHistoryController( this );
		
		createComponents();
		layoutComponents();
		
		initialiseComponents();
	}
	
	private void createComponents() {
		createVBox();
		createChartAreaPane();
	}
	
	private void createVBox() {
		vbox = new BorderPane();
		stocklistView = new StocklistView( controller );
		vbox.setTop( stocklistView );
		
		checkBoxListView = new ListView<CheckBox>();
		checkBoxListView.setItems( createCheckboxList() );
		vbox.setBottom( checkBoxListView );
	}
	
	private ObservableList<CheckBox> createCheckboxList() {
		ArrayList<CheckBox> arrayList = new ArrayList<CheckBox>();
		
		CheckBox SMA_10Day = new CheckBox( "10-day SMA" );
		SMA_10Day.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				controller.notify_SMA_10Day( newValue );
			}
			
		});
		arrayList.add( SMA_10Day );
		
		return FXCollections.observableArrayList( arrayList );
	}
	
	private void initialiseComponents() {
		ObservableList<ListedStockTO> observableList = controller.getListedStocks();
		populateStockListView( observableList );
	}
	
	private void layoutComponents() {
		super.setLeft( vbox );
		super.setCenter( chartAreaPane );
		
		layoutChartAreaPane();
	}
	
	private void createChartAreaPane() {
		chartAreaPane = new GridPane();
		chartAreaPane.setGridLinesVisible( false );
		
		stockPriceChart = new StockPriceChart();
		stockVolumeChart = new StockVolumeChart();
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
	
	private void populateStockListView( ObservableList<ListedStockTO> p_observableList ) {
		stocklistView.populateStockList( p_observableList );
	}
	
	public void populateStockPriceChart( XYChart.Series<Date, Number> p_closePriceSeries ){
		stockPriceChart.getData().clear();
		stockPriceChart.getData().add( p_closePriceSeries );
	}
	
	public void populateStockVolumeChart( XYChart.Series<String, Number> p_closePriceSeries ) {
		stockVolumeChart.getData().clear();
		stockVolumeChart.getData().add( p_closePriceSeries );
	}
	
	public void populateStockPriceAux( XYChart.Series<Date, Number> p_auxSeries ) {
		stockPriceChart.getData().add( p_auxSeries );
	}
	
	public void removeStockPriceAux( XYChart.Series<Date, Number> p_auxSeries ) {
		stockPriceChart.getData().remove( p_auxSeries );
	}

}
