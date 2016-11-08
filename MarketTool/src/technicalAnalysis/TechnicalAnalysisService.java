package technicalAnalysis;

import priceHistory.dataFeed.PriceHistoryTO;

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
	public void calculateSimpleMovingAverage( int p_numDays, PriceHistoryTO p_priceHistory ) {
		
		double movingAverage = 0;
		double numDays = (double) p_numDays;
		int size = p_priceHistory.numElements - 1;
		int reverseIndex = p_priceHistory.numElements - 1;
		boolean firstAverage = true;
		
		double[] movingAverageArray = new double[p_priceHistory.numElements];
		
		while ( reverseIndex > p_numDays ) {
			
			if ( firstAverage ) {
				movingAverage += p_priceHistory.closePrice[reverseIndex];
				if ( reverseIndex == (size - p_numDays) ) {
					movingAverage /= numDays;
					firstAverage = false;
					
					movingAverageArray[reverseIndex + p_numDays] = movingAverage;
				}
			} else {
				
				double addition = p_priceHistory.closePrice[reverseIndex] / numDays;
				double subtraction = p_priceHistory.closePrice[reverseIndex + p_numDays] / numDays;
				movingAverage += addition - subtraction;
				
				movingAverageArray[reverseIndex + p_numDays] = movingAverage;
			}
			
			reverseIndex--;
			
		}
		
		p_priceHistory.setSimpleMovingAverage( p_numDays, movingAverageArray );
		
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
	public void calculateExponentialMovingAverage( int p_numDays, PriceHistoryTO p_priceHistory ) {
		double movingAverage = 0;
		double previousAverage = 0;
		double numDays = (double)p_numDays;
		double alpha = 2 / (numDays+1);
		double[] movingAverageArray = new double[p_priceHistory.numElements];
		
		int size = p_priceHistory.numElements - 1;
		int index = 0;
		
		boolean firstAverage = true;
		
		while ( index < size ) {
			
			if ( firstAverage ) {
				movingAverage += p_priceHistory.closePrice[index];
				
				if ( index == (p_numDays - 1) ) {
					movingAverage /= numDays;
					
					previousAverage = movingAverage;
					movingAverageArray[index] = movingAverage;
					
					firstAverage = false;
				}
			} else {
				movingAverage = previousAverage + alpha * ( p_priceHistory.closePrice[index] - previousAverage );
				
				previousAverage = movingAverage;
				movingAverageArray[index] = movingAverage;
			}
			index++;
		}
		
		p_priceHistory.setExponentialMovingAverage( p_numDays, movingAverageArray );
	}
}
