package priceHistory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import applicationConstants.StringConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.CheckBox;
import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;
import technicalAnalysis.TechnicalAnalysisFacade;
import utility.DateUtility;
import view.BaseMainView;
import view.priceHistory.PriceHistoryMainView;

public class PriceHistoryController {
	
	private PriceHistoryFacade service;
	private PriceHistoryMainView view;
	
	private PriceHistoryTO selectedHistory;
	
	private int beginDateIndex = 0;
	
	private HashMap<String, XYChart.Series<Date, Number>> technicalAnalysisCache = new HashMap<String, XYChart.Series<Date, Number>>();
	
	public PriceHistoryController( PriceHistoryMainView p_view ) {
		service = new PriceHistoryFacade();
		view = p_view;
	}
	
	public ObservableList<ListedStockTO> getListedStocks() {
		List<ListedStockTO> listedStocks = service.getListedStocks();
		return FXCollections.observableArrayList( listedStocks );
	}
	
	public void selectStock( String p_ticker ) {
		selectedHistory = null;
		resetSelections();
		
		selectedHistory = service.getAllPriceHistory( p_ticker );
		
		showBasicHistory( selectedHistory );
	}
	
	private void showBasicHistory( PriceHistoryTO p_selectedHistory ) {
		XYChart.Series<Date, Number> closePriceSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
		
		beginDateIndex = findIndexOfDefaultDate( p_selectedHistory ).intValue();
		int index = beginDateIndex;
		
		Date thisDate;
		
		while ( index < p_selectedHistory.numElements - 1 ) {
			thisDate = DateUtility.parseStringToDate( p_selectedHistory.date[index] );
			
			Data<Date, Number> priceData = new Data<Date, Number>( thisDate, p_selectedHistory.closePrice[index] );
			closePriceSeries.getData().add( priceData );
			
			Data<String, Number> volumeData = new Data<String, Number>( p_selectedHistory.date[index], p_selectedHistory.volume[index]/100000 );
			volumeSeries.getData().add( volumeData );
			
			index++;
		}
		
		view.populateStockPriceChart( closePriceSeries );
		view.populateStockVolumeChart( volumeSeries );
		
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

	public void notify_EMA( Boolean newValue, int p_numDays ) {
		if ( newValue == true ) {
			XYChart.Series<Date, Number> emaSeries = new XYChart.Series<>();
			
			TechnicalAnalysisFacade techFacade = new TechnicalAnalysisFacade();
			techFacade.calculateExponentialMovingAverage( selectedHistory, p_numDays );
			
			Date thisDate;
			int index = beginDateIndex;
			while ( index < selectedHistory.numElements - 1 ) {
				thisDate = DateUtility.parseStringToDate( selectedHistory.date[index] );				
				
				Data<Date, Number> emaDayData = new Data<Date, Number>( thisDate, selectedHistory.getExponentialMovingAverage(p_numDays)[index] );
				emaSeries.getData().add( emaDayData );
				
				index++;
			}
			technicalAnalysisCache.put( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + p_numDays, emaSeries );
			view.populateStockPriceAux( emaSeries );
		} else if ( newValue == false ) {
			view.removeStockPriceAux( technicalAnalysisCache.get(StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + p_numDays) );
		}
		
	}
	
	public void notify_SMA( Boolean newValue, int p_numDays ) {
		if ( newValue == true ) {
			XYChart.Series<Date, Number> smaSeries = new XYChart.Series<>();
			
			TechnicalAnalysisFacade techFacade = new TechnicalAnalysisFacade();
			techFacade.calculateSimpleMovingAverage( selectedHistory, p_numDays );
			
			Date thisDate;
			int index = beginDateIndex;
			while ( index < selectedHistory.numElements - 1 ) {
				thisDate = DateUtility.parseStringToDate( selectedHistory.date[index] );				
				
				Data<Date, Number> smaDayData = new Data<Date, Number>( thisDate, selectedHistory.getSimpleMovingAverage(p_numDays)[index] );
				smaSeries.getData().add( smaDayData );
				
				index++;
			}
			technicalAnalysisCache.put( StringConstants.SIMPLEMOVINGAVERAGE_DAYS + p_numDays, smaSeries );
			view.populateStockPriceAux( smaSeries );
		} else if ( newValue == false ) {
			view.removeStockPriceAux( technicalAnalysisCache.get(StringConstants.SIMPLEMOVINGAVERAGE_DAYS + p_numDays) );
		}
		
	}
	
	private void resetSelections() {
		for ( CheckBox checkBox : view.techAnalysisSelectionListView.getItems() ) {
			checkBox.setSelected( false );
		}
	}

}
