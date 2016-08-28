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
		
		Date l_thisDate;
		
		List<DataFeedTO> priceHistory = service.getPriceChartData( p_ticker, null, null );
		
		for ( DataFeedTO dataTO : priceHistory ) {
			l_thisDate = DateUtility.parseStringToDate( dataTO.getDate() );
			Data<Date, Number> data = new Data<Date, Number>( l_thisDate, (Number)dataTO.getClosePrice() );
			
			closePriceSeries.getData().add( data );
		}

		return closePriceSeries;
	}

}
