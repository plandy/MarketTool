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
	
	private PriceHistoryService service;
	private PriceHistoryView view;
	
	public PriceHistoryController( PriceHistoryView p_view ) {
		service = PriceHistoryService.INSTANCE;
		view = p_view;
	}
	
	public ObservableList<ListedStockTO> getListedStocks() {
		List<ListedStockTO> listedStocks = service.getListedStocks();		
		return FXCollections.observableArrayList( listedStocks );
	}
	
	public void selectStock( String p_ticker ) {
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
		
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.parseStringToDate("2015-08-29");
		
		List<DataFeedTO> priceHistory = service.getPriceChartData( p_ticker, beginDate, todayDate );
		Date l_thisDate;
		
		for ( DataFeedTO dataTO : priceHistory ) {
			l_thisDate = DateUtility.parseStringToDate( dataTO.getDate() );
			
			Data<Date, Number> priceData = new Data<Date, Number>( l_thisDate, (Number)dataTO.getClosePrice() );
			closePriceSeries.getData().add( priceData );
			
			Data<String, Number> volumeData = new Data<String, Number>( dataTO.getDate(), (Number)(dataTO.getVolume()/100000) );
			volumeSeries.getData().add( volumeData );
		}
		
		view.populateStockPriceChart(closePriceSeries);
		view.populateStockVolumeChart(volumeSeries);
		
	}

}
