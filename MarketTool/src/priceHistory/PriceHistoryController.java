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
			l_thisDate = dataTO.getDateAsDate();
			
			Data<Date, Number> priceData = new Data<Date, Number>( l_thisDate, (Number)dataTO.getClosePrice() );
			closePriceSeries.getData().add( priceData );
			
			Data<String, Number> volumeData = new Data<String, Number>( dataTO.getDateAsString(), (Number)(dataTO.getVolume()/100000) );
			volumeSeries.getData().add( volumeData );
		}
		
		view.populateStockPriceChart( closePriceSeries );
		view.populateStockVolumeChart( volumeSeries );
	}
	
	private Integer findIndexOfDefaultDate( List<DataFeedTO> p_selectedHistory ) {
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
	private Integer findIndexByDateLinearSearch( List<DataFeedTO> p_selectedHistory, Date p_dateToFind ) {
		
		int index = p_selectedHistory.size();
		
		boolean isFound = false;
		boolean isBefore = false;
		
		while ( !isFound ) {
			index--;
			
			DataFeedTO dataPoint = p_selectedHistory.get( index );
			isBefore = DateUtility.isBeforeCalendarDate(dataPoint.getDateAsDate(), p_dateToFind);
			
			if ( isBefore == false ) {
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
