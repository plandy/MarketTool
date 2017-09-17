package view.menuBar.dataFeedMenu.dataFeedMenuItems;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryFacade;
import priceHistory.dataFeed.DataFeedTO;
import view.ProgressBlockingPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BulkDataCollectItem extends MenuItem {

    private ProgressBlockingPopup progressPopup;


    public BulkDataCollectItem( MenuBar p_parentMenuBar ) {
        super( "Bulk collect data" );

        this.setOnAction( new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                BulkDataCollectionOrchestrator bulkDataCollector = new BulkDataCollectionOrchestrator(3);

                progressPopup = new ProgressBlockingPopup( p_parentMenuBar.getScene().getWindow() );
                progressPopup.show();

                bulkDataCollector.start();
            }
        });

    }

    public class BulkDataCollectionOrchestrator extends Thread {

        private final List<ListedStockTO> stocks;
        private final List<List<ListedStockTO>> listOfJobs;
        private final List<BulkDataCollectionWorker> workers;
        private final ArrayBlockingQueue<List<DataFeedTO>>listDataToBeInserted;

        public BulkDataCollectionOrchestrator( int p_numWorkers ) {
            stocks = new PriceHistoryFacade().getListedStocks();
            workers = new ArrayList<BulkDataCollectionWorker>( p_numWorkers );
            listDataToBeInserted = new ArrayBlockingQueue( stocks.size() );
            listOfJobs = new ArrayList<List<ListedStockTO>>( p_numWorkers );

            for ( int i = 0; i < p_numWorkers; i++ ) {
                listOfJobs.add( new ArrayList<ListedStockTO>() );
            }

            int workerOrdinal;
            for ( int index = 0; index < stocks.size(); index++ ) {
                workerOrdinal = index % p_numWorkers;
                listOfJobs.get(workerOrdinal).add( stocks.get(index) );
            }

            for ( int i = 0; i < p_numWorkers; i++  ) {
                BulkDataCollectionWorker worker = new BulkDataCollectionWorker( listOfJobs.get(i), this );
                workers.add( worker );
            }

        }

        private void SQLITE_ISNT_FOR_CONCURRENT_ACCESS() {
            while ( listDataToBeInserted.size() < stocks.size() ) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                updateProgress1();
            }
        }

        @Override
        public void run() {

            int numInsertsRemaining = stocks.size();

            for ( BulkDataCollectionWorker worker : workers ) {
                worker.start();
            }

            PriceHistoryFacade facade = new PriceHistoryFacade();

            SQLITE_ISNT_FOR_CONCURRENT_ACCESS();

            while ( numInsertsRemaining > 0 ) {
                List<DataFeedTO> job = null;
                try {
                    job = listDataToBeInserted.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if ( job.isEmpty() == false ) {
                    facade.insertPriceHistory( job.get(0).getTicker(), job );
                    updateProgress2( numInsertsRemaining );
                }

                numInsertsRemaining--;
            }

            finish();
        }

        private void finish() {
            Platform.runLater(() -> {
                progressPopup.close();
            });
        }

        public void workerOnFinishJob( List<DataFeedTO> p_dataToInsert ) {
            try {
                listDataToBeInserted.put( p_dataToInsert );
            } catch ( InterruptedException exception ) {
                throw new RuntimeException();
            }
        }

        private static final int DATAFEEDACCESS_COST = 10;
        private static final int DATABASEINSERT_COST = 1;

        private void updateProgress1() {
            double workEstimate = ( stocks.size() * DATAFEEDACCESS_COST ) + ( stocks.size() * DATABASEINSERT_COST );
            double currentWorkDone = listDataToBeInserted.size() * DATAFEEDACCESS_COST;
            double progress = currentWorkDone / workEstimate;

            progressPopup.updateProgress( progress );
            System.out.println("Update Progress 1");
        }

        private void updateProgress2( int p_numInsertsRemaining ) {
            double workEstimate = ( stocks.size() * DATAFEEDACCESS_COST ) + ( stocks.size() * DATABASEINSERT_COST );
            double currentWorkDone = ( stocks.size() * DATAFEEDACCESS_COST ) + ( (stocks.size() - p_numInsertsRemaining) * DATABASEINSERT_COST );
            double progress = currentWorkDone / workEstimate;

            progressPopup.updateProgress( progress );
        }

    }

    public class BulkDataCollectionWorker extends Thread {

        private final List<ListedStockTO> listedStocks;
        private final BulkDataCollectionOrchestrator orchestrator;

        public BulkDataCollectionWorker( List<ListedStockTO> p_listedStocks, BulkDataCollectionOrchestrator p_orchestrator ) {
            listedStocks = p_listedStocks;
            orchestrator = p_orchestrator;
        }

        @Override
        public void run() {

            PriceHistoryFacade facade = new PriceHistoryFacade();

            for (ListedStockTO stock : listedStocks) {
                List<DataFeedTO> history = facade.getMissingHistoryFromDataFeed( stock.getTicker() );
                orchestrator.workerOnFinishJob( history );
            }
        }
    }

}