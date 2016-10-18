package technicalAnalysis;

import java.math.BigDecimal;
import java.util.List;

import priceHistory.dataFeed.DataFeedTO;

public class TechnicalAnalysisService {
	
	public TechnicalAnalysisService() {
		
	}
	
	/**
	 * Sets a simple moving average on the priceHistory using the specified period.
	 * 
	 * <p>Works backwards from the most recent date in the history, data points with less previous history than the
	 * <b>p_numDays</b> will have null SMA value given.
	 * 
	 * @param p_numDays
	 * @param p_priceHistory
	 */
	public void calculateSimpleMovingAverage( int p_numDays, List<DataFeedTO> p_priceHistory ) {
		
		BigDecimal movingAverage = new BigDecimal( 0 );
		BigDecimal numDays = new BigDecimal( p_numDays );
		int size = p_priceHistory.size() - 1;
		int reverseIndex = size;
		boolean firstAverage = true;
		
		while ( reverseIndex > p_numDays ) {
			DataFeedTO currentDataPoint = p_priceHistory.get( reverseIndex );
			
			if ( firstAverage ) {
				movingAverage = movingAverage.add( currentDataPoint.getClosePrice() );
				if ( reverseIndex == (size - p_numDays) ) {
					movingAverage = ( movingAverage.divide(numDays) );
					firstAverage = false;
					
					p_priceHistory.get( reverseIndex + p_numDays ).setSimpleMovingAverage( p_numDays, movingAverage );
				}
			} else {
				
				BigDecimal addition = currentDataPoint.getClosePrice().divide( numDays );
				BigDecimal subtraction = p_priceHistory.get( reverseIndex + p_numDays ).getClosePrice().divide( numDays );
				movingAverage = movingAverage.add( addition ).subtract( subtraction );
				
				p_priceHistory.get( reverseIndex + p_numDays ).setSimpleMovingAverage( p_numDays, movingAverage );
			}
			
			reverseIndex--;
			
		}
		
	}
	
	/**
	 * Sets an exponential moving average on the priceHistory using the specified period.
	 * 
	 * <p>Initialises first average by calculating a <b>Simple Moving Average</b>. Data points with less previous history than the
	 * <b>p_numDays</b> will have null EMA value given.
	 * 
	 * @param p_numDays
	 * @param p_priceHistory
	 */
	public void calculateExponentialMovingAverage( int p_numDays, List<DataFeedTO> p_priceHistory ) {
		BigDecimal movingAverage = new BigDecimal( 0 );
		BigDecimal previousAverage = new BigDecimal( 0 );
		BigDecimal alpha = new BigDecimal( 0 );
		BigDecimal numDays = new BigDecimal( p_numDays );
		int size = p_priceHistory.size() - 1;
		int index = 0;
		
		boolean firstAverage = true;
		
		while ( index < size ) {
			DataFeedTO currentDataPoint = p_priceHistory.get( index );
			
			if ( firstAverage ) {
				
				movingAverage = movingAverage.add( currentDataPoint.getClosePrice() );
				
				if ( index == (p_numDays - 1) ) {
					movingAverage = movingAverage.divide( numDays );
					
					previousAverage = movingAverage;
					currentDataPoint.setExponentialMovingAverage( p_numDays, movingAverage );
				}
			} else {
				movingAverage = previousAverage.add( alpha.multiply(currentDataPoint.getClosePrice().subtract(previousAverage)) );
				
				previousAverage = movingAverage;
				currentDataPoint.setExponentialMovingAverage( p_numDays, movingAverage );
			}
			
		}
		
	}
}
