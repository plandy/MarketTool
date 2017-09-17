package applicationConstants;

import java.util.ArrayList;
import priceHistory.ListedStockTO;

public class InitialListedStocks {
	
	public static ArrayList<ListedStockTO> listedStocks;
	
	static {
		listedStocks = new ArrayList<ListedStockTO>();
		
		listedStocks.add( new ListedStockTO("AAPL", "Apple Inc.") );
		listedStocks.add( new ListedStockTO("BABA", "Alibaba Group Holding Limited") );
		listedStocks.add( new ListedStockTO("BAC", "Bank pf America Corporation") );
		listedStocks.add( new ListedStockTO("C", "Citigroup Inc.") );
		listedStocks.add( new ListedStockTO("F", "Ford Motor Co.") );
		listedStocks.add( new ListedStockTO("FB", "Facebook, Inc.") );
		listedStocks.add( new ListedStockTO("GE", "General Electric Company") );
		listedStocks.add( new ListedStockTO("GM", "General Motors Company") );
		listedStocks.add( new ListedStockTO("HPQ", "HP Inc.") );
		listedStocks.add( new ListedStockTO("IBM", "International Business Machines Corporation") );
		listedStocks.add( new ListedStockTO("JPM", "JPMorgan Chase & Co.") );
		listedStocks.add( new ListedStockTO("MRO", "Marathon Oil Corporation") );
		listedStocks.add( new ListedStockTO("MSFT", "Microsoft Corporation") );
		listedStocks.add( new ListedStockTO("PFE", "Pfizer Inc.") );
		listedStocks.add( new ListedStockTO("PG", "The Procter & Gamble Company") );
		listedStocks.add( new ListedStockTO("VZ", "Verizon Communications Inc.") );
	}

}
