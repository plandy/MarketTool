package applicationConstants;

import java.util.ArrayList;

import priceHistory.ListedStockTO;

public class InitialListedStocks {
	
	public static ArrayList<ListedStockTO> listedStocks;
	
	static {
		listedStocks = new ArrayList<ListedStockTO>();
		
		listedStocks.add( new ListedStockTO("IBM", "International Business Machines Corporation") );
	}

}
