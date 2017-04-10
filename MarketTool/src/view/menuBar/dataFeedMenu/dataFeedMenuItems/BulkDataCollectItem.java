package view.menuBar.dataFeedMenu.dataFeedMenuItems;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryFacade;
import priceHistory.dataFeed.DataFeedTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BulkDataCollectItem extends MenuItem {

    public BulkDataCollectItem() {
        super( "Bulk collect data" );
        this.setOnAction( new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                BulkDataCollectionOrchestrator bulkDataCollector = new BulkDataCollectionOrchestrator(3);
                bulkDataCollector.start();

                final Stage myDialog = new Stage();
                myDialog.initModality(Modality.APPLICATION_MODAL);

                ProgressBar progressBar = new ProgressBar(0);

                Scene myDialogScene = new Scene(VBoxBuilder.create()
                        .children(new Text("getting data"), progressBar)
                        .alignment(Pos.CENTER)
                        .build());

                myDialog.setScene(myDialogScene);
                myDialog.show();

                while( bulkDataCollector.isFinished() == false ) {
                    progressBar.setProgress( 5 );
                }

                myDialog.close();
            }

        });
    }

    public class BulkDataCollectionOrchestrator extends Thread {

        private final List<ListedStockTO> stocks;
        private final List<List<ListedStockTO>> listOfJobs;
        private final List<BulkDataCollectionWorker> workers;
        private final ArrayBlockingQueue<List<DataFeedTO>>listDataToBeInserted;

        private volatile int numJobsRemaining;

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

            numJobsRemaining = stocks.size();

        }

        private void SQLITE_ISNT_FOR_CONCURRENT_ACCESS() {
            while ( listDataToBeInserted.size() < stocks.size() ) {
            }
        }

        @Override
        public void run() {

            for ( BulkDataCollectionWorker worker : workers ) {
                worker.start();
            }

            PriceHistoryFacade facade = new PriceHistoryFacade();

            SQLITE_ISNT_FOR_CONCURRENT_ACCESS();

            while ( numJobsRemaining > 0 ) {

                List<DataFeedTO> job = null;
                try {
                    job = listDataToBeInserted.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if ( job.isEmpty() == false ) {
                    facade.insertPriceHistory( job.get(0).getTicker(), job );
                }

                numJobsRemaining--;

            }
        }

        public void workerOnFinishJob( List<DataFeedTO> p_dataToInsert ) {
            try {
                listDataToBeInserted.put( p_dataToInsert );
            } catch ( InterruptedException exception ) {
                throw new RuntimeException();
            }
        }

        public boolean isFinished() {

            boolean isFinished = false;

            isFinished = ( numJobsRemaining == 0 );

            return isFinished;

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
