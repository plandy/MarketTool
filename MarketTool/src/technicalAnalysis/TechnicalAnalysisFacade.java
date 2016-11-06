package technicalAnalysis;

import java.util.List;

import priceHistory.dataFeed.DataFeedTO;
import priceHistory.dataFeed.PriceHistoryTO;

public class TechnicalAnalysisFacade {
	
	TechnicalAnalysisService service;
	
	public TechnicalAnalysisFacade() {
		service = new TechnicalAnalysisService();
	}
	
	/**
	 * Calculates a Simple Moving Average for the given period
	 * 
	 * @param p_priceHistory
	 * @param p_numDays
	 */
	public void calculateSimpleMovingAverage( PriceHistoryTO p_priceHistory, int p_numDays ) {
		service.calculateSimpleMovingAverage( p_numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_10Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 10;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_20Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 20;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_50Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 50;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_100Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 100;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_200Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 200;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	/**
	 * Calculates an Exponential Moving Average for the given period
	 * 
	 * @param p_priceHistory
	 * @param p_numDays
	 */
	public void calculateExponentialMovingAverage( PriceHistoryTO p_priceHistory, int p_numDays ) {
		service.calculateExponentialMovingAverage( p_numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_10Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 10;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_20Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 20;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_50Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 50;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_100Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 100;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_200Day( PriceHistoryTO p_priceHistory ) {
		int numDays = 200;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
}
