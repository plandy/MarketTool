package priceHistory;

public class ListedStockTO {
	
	private final String ticker;
	private final String fullName;
	private boolean isWatchlisted;
	
	public ListedStockTO( String p_ticker, String p_fullName, boolean p_isWatchlisted ) {
		ticker = p_ticker;
		fullName = p_fullName;
		isWatchlisted = p_isWatchlisted;
	}

	public ListedStockTO( String p_ticker, String p_fullName ) {
		this( p_ticker, p_fullName, false );
	}
	
	@Override
	public String toString() {
		return ticker;
	}
	
	public String getTicker() {
		return ticker;
	}
	
	public String getFullname() {
		return fullName;
	}

	public boolean isWatchlisted() {
		return isWatchlisted;
	}
	public void setIsWatchListed( boolean p_isWatchlisted ) {
		isWatchlisted = p_isWatchlisted;
	}
	
}
