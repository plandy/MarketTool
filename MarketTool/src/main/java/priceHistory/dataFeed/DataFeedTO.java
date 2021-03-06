package priceHistory.dataFeed;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import applicationConstants.StringConstants;
import utility.DateUtility;

public class DataFeedTO {
	
	public DataFeedTO() {
		
	}
	
	private String ticker;
	private String date;
	private BigDecimal openPrice;
	private BigDecimal highPrice;
	private BigDecimal lowPrice;
	private BigDecimal closePrice;
	private int volume;
	
	private HashMap<String, Object> map = new HashMap<String, Object>();
	
	private void setValue( String p_key, Object p_value ) {
		map.put(p_key, p_value);
	}
	
	private Object getValue( String p_key ) {
		return map.get( p_key );
	}
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getDateAsString() {
		return date;
	}
	
	public Date getDateAsDate() {
		return DateUtility.parseStringToDate( date );
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public BigDecimal getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}
	public BigDecimal getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}
	public BigDecimal getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}
	public BigDecimal getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public void setSimpleMovingAverage( int p_numDays, BigDecimal p_movingAverage ) {
		setValue( StringConstants.SIMPLEMOVINGAVERAGE_DAYS + String.valueOf(p_numDays), p_movingAverage );
	}
	
	public BigDecimal getSimpleMovingAverage( int p_numDays ) {
		return (BigDecimal) getValue( StringConstants.SIMPLEMOVINGAVERAGE_DAYS + String.valueOf(p_numDays) );
	}
	
	public void setExponentialMovingAverage( int p_numDays, BigDecimal p_movingAverage ) {
		setValue( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + String.valueOf(p_numDays), p_movingAverage );
	}
	
	public BigDecimal getExponentialMovingAverage( int p_numDays ) {
		return (BigDecimal) getValue( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + String.valueOf(p_numDays) );
	}
	
}
