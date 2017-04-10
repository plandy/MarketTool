package priceHistory.dataFeed.backgroundService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

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
		
		private volatile boolean l_interrupt = false;

		//SQLite will throw an SQLITE_BUSY error on concurrent access, regardless of multithreading modes.
		//I am extremely annoyed about this right now, so rather than rewrite everything I am just limiting the
		//connection pool size = 1 and sleeping this thread to prevent it hogging the resource
		//Eventually I will have to handle this properly...
		private static final long FIFTY_MILLIS = 50000000;

		@Override
		public void run() {
			
			PriceHistoryFacade priceHistoryFacade = new PriceHistoryFacade();
			List<ListedStockTO> listedStocks = priceHistoryFacade.getListedStocks();
			for ( ListedStockTO stock : listedStocks ) {
				if ( l_interrupt == true ) {
					break;
				}
				//priceHistoryFacade.getHistoryFromDataFeed( stock.getTicker() );
				LockSupport.parkNanos( FIFTY_MILLIS );
			}
			
		}
		
		private void stop() {
			l_interrupt = true;
		}
		
	}
	
}
