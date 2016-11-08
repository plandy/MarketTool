package priceHistory.dataFeed;

import java.util.HashMap;

import applicationConstants.StringConstants;

public class PriceHistoryTO {
	
	public PriceHistoryTO() {
	}
	
	public String ticker;
	public String[] date;
	public double[] openPrice;
	public double[] highPrice;
	public double[] lowPrice;
	public double[] closePrice;
	public int[] volume;
	
	private HashMap<String, double[]> map = new HashMap<String, double[]>();
	
	//Java array.length returns the capacity of the array. Therefore, using this to count the number of quotes
	//actually existing in this history
	public int numElements;
	
	public void initialiseArrays( int p_size ) {
		date = new String[p_size];
		openPrice = new double[p_size];
		highPrice = new double[p_size];
		lowPrice = new double[p_size];
		closePrice = new double[p_size];
		volume = new int[p_size];
		
		numElements = p_size;
	}
	
	private void setValue( String p_key, double[] p_value ) {
		map.put(p_key, p_value);
	}
	
	private double[] getValue( String p_key ) {
		return map.get( p_key );
	}
	
	public void setSimpleMovingAverage( int p_numDays, double[] p_movingAverage ) {
		setValue( StringConstants.SIMPLEMOVINGAVERAGE_DAYS + String.valueOf(p_numDays), p_movingAverage );
	}
	
	public double[] getSimpleMovingAverage( int p_numDays ) {
		return getValue( StringConstants.SIMPLEMOVINGAVERAGE_DAYS + String.valueOf(p_numDays) );
	}
	
	public void setExponentialMovingAverage( int p_numDays, double[] p_movingAverage ) {
		setValue( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + String.valueOf(p_numDays), p_movingAverage );
	}
	
	public double[] getExponentialMovingAverage( int p_numDays ) {
		return getValue( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + String.valueOf(p_numDays) );
	}
	
}
