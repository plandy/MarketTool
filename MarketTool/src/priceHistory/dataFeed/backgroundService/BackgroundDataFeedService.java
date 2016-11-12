package priceHistory.dataFeed.backgroundService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryFacade;

public class BackgroundDataFeedService {
	
	private ScheduledExecutorService scheduledService;
	private DataRequestRunnable dataRequestRunnable;
	
	public BackgroundDataFeedService() {
	}
	
	public void start() {
		scheduledService = Executors.newScheduledThreadPool( 1 );
		dataRequestRunnable = new DataRequestRunnable();
		scheduledService.scheduleAtFixedRate( dataRequestRunnable, 0, 600, TimeUnit.SECONDS );
	}
	
	public void stop() {
		dataRequestRunnable.stop();
		scheduledService.shutdown();
	}
	
	private class DataRequestRunnable implements Runnable {
		
		volatile boolean l_interrupt = false;

		@Override
		public void run() {
			
			PriceHistoryFacade priceHistoryFacade = new PriceHistoryFacade();
			List<ListedStockTO> listedStocks = priceHistoryFacade.getListedStocks();
			for ( ListedStockTO stock : listedStocks ) {
				if ( l_interrupt == true ) {
					break;
				}
				priceHistoryFacade.getHistoryFromDataFeed( stock.getTicker() );
			}
			
		}
		
		public void stop() {
			l_interrupt = true;
		}
		
	}
	
}
