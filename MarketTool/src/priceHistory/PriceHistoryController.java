package priceHistory;

import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;
import technicalAnalysis.TechnicalAnalysisFacade;
import utility.DateUtility;

public class PriceHistoryController {
	
	private PriceHistoryFacade service;
	private PriceHistoryView view;
	
	private PriceHistoryTO selectedHistory;
	
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
		
		selectedHistory = service.getAllPriceHistory( p_ticker );
		
		showBasicHistory( selectedHistory );
	}
	
	private void showBasicHistory( PriceHistoryTO p_selectedHistory ) {
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
		XYChart.Series<Date, Number> sma100DaySeries = new XYChart.Series<>();
		
		int beginDateIndex = findIndexOfDefaultDate( p_selectedHistory ).intValue();
		
		TechnicalAnalysisFacade techFacade = new TechnicalAnalysisFacade();
		techFacade.calculateExponentialMovingAverage_10Day( p_selectedHistory );
		
		Date thisDate;
		
		while ( beginDateIndex < p_selectedHistory.numElements - 1 ) {
			thisDate = DateUtility.parseStringToDate( p_selectedHistory.date[beginDateIndex] );
			
			Data<Date, Number> priceData = new Data<Date, Number>( thisDate, p_selectedHistory.closePrice[beginDateIndex] );
			closePriceSeries.getData().add( priceData );
			
			Data<String, Number> volumeData = new Data<String, Number>( p_selectedHistory.date[beginDateIndex], p_selectedHistory.volume[beginDateIndex]/100000 );
			volumeSeries.getData().add( volumeData );
			
			Data<Date, Number> sma100DayData = new Data<Date, Number>( thisDate, p_selectedHistory.getExponentialMovingAverage(10)[beginDateIndex] );
			sma100DaySeries.getData().add( sma100DayData );
			
			beginDateIndex++;
		}
		
		view.populateStockPriceChart( closePriceSeries );
		view.populateStockVolumeChart( volumeSeries );
		
		view.populateStockPriceAux( sma100DaySeries );
	}
	
	private Integer findIndexOfDefaultDate( PriceHistoryTO p_selectedHistory ) {
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.addYears( todayDate, -1 );
		
		return findIndexByDateLinearSearch( p_selectedHistory, beginDate );
	}
	
	/**
	 * Finds the first datapoint in the history with date <= <b>p_dateToFind</b>
	 * 
	 * @param p_selectedHistory
	 * @param p_dateToFind
	 * @return
	 */
	private Integer findIndexByDateLinearSearch( PriceHistoryTO p_selectedHistory, Date p_dateToFind ) {
		
		int index = p_selectedHistory.numElements;
		
		boolean isFound = false;
		boolean isBefore = false;
		
		while ( !isFound ) {
			index--;
			
			String date =  p_selectedHistory.date[index];
			isBefore = DateUtility.isBeforeCalendarDate( DateUtility.parseStringToDate(date), p_dateToFind );
			
			if ( isBefore == true ) {
				isFound = true;
			}
		}
		
		return index;
	}
	
	//TODO finish
	private Integer findIndexByDateBinarySearch( List<DataFeedTO> p_selectedHistory ) {
		
		Date todayDate = DateUtility.getTodayDate();
		Date beginDate = DateUtility.addYears( todayDate, -1 );
		
		int listSize = p_selectedHistory.size();
		int index = listSize / 2;
		
		boolean isFound = false;
		
		while ( !isFound ) {
			DataFeedTO dataPoint = p_selectedHistory.get( index );
			if ( DateUtility.isSameCalendarDate(dataPoint.getDateAsDate(), beginDate) ) {
				isFound = true;
			} else if ( DateUtility.isBeforeCalendarDate(dataPoint.getDateAsDate(), beginDate) ) {
				index = ( index + listSize ) / 2;
			} else {
				index = index / 2;
			}
		}
		
		return index;
	}

}
