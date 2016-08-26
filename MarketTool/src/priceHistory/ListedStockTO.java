package priceHistory;

public class ListedStockTO {
	
	private String ticker;
	private String fullName;
	
	public ListedStockTO( String p_ticker, String p_fullName ) {
		ticker = p_ticker;
		fullName = p_fullName;
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
	
}
