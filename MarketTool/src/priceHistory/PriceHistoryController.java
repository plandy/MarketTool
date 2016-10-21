package priceHistory;

import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import priceHistory.dataFeed.DataFeedTO;
import utility.DateUtility;

public class PriceHistoryController {
	
	private PriceHistoryFacade service;
	private PriceHistoryView view;
	
	private List<DataFeedTO> selectedHistory;
	
	public PriceHistoryController( PriceHistoryView p_view ) {
		service = new PriceHistoryFacade();
		view = p_view;
	}
	
	public ObservableList<ListedStockTO> getListedStocks() {
		List<ListedStockTO> listedStocks = service.getListedStocks();		
		return FXCollections.observableArrayList( listedStocks );
	}
	
	public void selectStock( String p_ticker ) {
		selectedHistory = null;
		
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.parseStringToDate("2015-08-29");
		
		//selectedHistory = service.getPriceChartData( p_ticker, beginDate, todayDate );
		selectedHistory = service.getAllPriceHistory( p_ticker );
			
		showBasicHistory( selectedHistory );
	}
	
	private void showBasicHistory( List<DataFeedTO> p_selectedHistory ) {
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
		
		Date l_thisDate;
		for ( DataFeedTO dataTO : p_selectedHistory ) {
			l_thisDate = DateUtility.parseStringToDate( dataTO.getDate() );
			
			Data<Date, Number> priceData = new Data<Date, Number>( l_thisDate, (Number)dataTO.getClosePrice() );
			closePriceSeries.getData().add( priceData );
			
			Data<String, Number> volumeData = new Data<String, Number>( dataTO.getDate(), (Number)(dataTO.getVolume()/100000) );
			volumeSeries.getData().add( volumeData );
		}
		
		view.populateStockPriceChart( closePriceSeries );
		view.populateStockVolumeChart( volumeSeries );
	}

}
