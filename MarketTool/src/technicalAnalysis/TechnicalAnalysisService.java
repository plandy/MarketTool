package technicalAnalysis;

import java.math.BigDecimal;
import java.util.List;

import priceHistory.dataFeed.DataFeedTO;

public class TechnicalAnalysisService {
	
	public TechnicalAnalysisService() {
		
	}
	
	/**
	 * Sets a simple moving average on the priceHistory using the specified period.
	 * <p>
	 * Works backwards from the most recent date in the history, data points with less previous history than the
	 * <b>p_numDays</b> will have null SMA value given.
	 * 
	 * @param p_numDays
	 * @param p_priceHistory
	 */
	public static void calculateSimpleMovingAverage( int p_numDays, List<DataFeedTO> p_priceHistory ) {
		
		BigDecimal movingAverage = new BigDecimal( 0 );
		BigDecimal numDays = new BigDecimal( p_numDays );
		int size = p_priceHistory.size();
		int reverseIndex = size;
		boolean firstAverage = true;
		
		while ( reverseIndex > p_numDays ) {
			DataFeedTO currentDataPoint = p_priceHistory.get( reverseIndex );
			
			if ( firstAverage ) {
				movingAverage.add( currentDataPoint.getClosePrice() );
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
			
		}
		
	}
}
