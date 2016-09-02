package priceHistory;

import java.util.Date;
import java.util.List;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import priceHistory.dataFeed.DataFeedTO;
import utility.DateUtility;

public class PriceHistoryController {
	
	private PriceHistoryService service;
	
	public PriceHistoryController() {
		service = PriceHistoryService.INSTANCE;
	}
	
	public XYChart.Series<Date, Number> getPriceChartData( String p_ticker ) {
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.parseStringToDate("2015-08-29");
		
		List<DataFeedTO> priceHistory = service.getPriceChartData( p_ticker, beginDate, todayDate );
		Date l_thisDate;
		
		for ( DataFeedTO dataTO : priceHistory ) {
			l_thisDate = DateUtility.parseStringToDate( dataTO.getDate() );
			Data<Date, Number> data = new Data<Date, Number>( l_thisDate, (Number)dataTO.getClosePrice() );
			
			closePriceSeries.getData().add( data );
		}

		return closePriceSeries;
	}
	
	public XYChart.Series<String, Number> getVolumeChartData( String p_ticker ) {
		XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
		
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.parseStringToDate("2015-08-29");
		
		List<DataFeedTO> priceHistory = service.getPriceChartData( p_ticker, beginDate, todayDate );
		
		for ( DataFeedTO dataTO : priceHistory ) {
			Data<String, Number> data = new Data<String, Number>( dataTO.getDate(), (Number)(dataTO.getVolume()/100000) );
			
			volumeSeries.getData().add( data );
		}

		return volumeSeries;
	}

}
