package view.priceHistory;

import applicationConstants.StringConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import priceHistory.ListedStockTO;
import priceHistory.PriceHistoryController;

public class StocklistView extends TabPane {
	
	PriceHistoryController controller;
	
	private Tab stocklistTab;
	private ListView<ListedStockTO> stocklistListView;
	
	private Tab watchlistTab;
	private ListView<ListedStockTO> watchlistListView;
	
	public StocklistView( PriceHistoryController p_controller ) {
		super();
		
		controller = p_controller;
		
		addTabListener();
		createChildTabs();
		
		this.getSelectionModel().select( stocklistTab );
	}
	
	public void populateStockList( ObservableList<ListedStockTO> p_observableList ) {
		stocklistListView.setItems( p_observableList );
		populateWatchList( p_observableList );
	}

	private void populateWatchList( ObservableList<ListedStockTO> p_stockList ) {

		ObservableList<ListedStockTO> watchlist = FXCollections.observableArrayList();

		for ( ListedStockTO stockTO : p_stockList ) {
			if ( stockTO.isWatchlisted() == true ) {
				watchlist.add( stockTO );
			}
		}

		watchlistListView.setItems( watchlist );
	}
	
	private void createChildTabs() {
		createStockListTab();
		createWatchListTab();
	}
	
	private void createStockListTab() {
		stocklistListView = new ListView<ListedStockTO>();
		
		stocklistTab = new Tab();
		stocklistTab.setContent( stocklistListView );
		stocklistTab.setText( StringConstants.PRICEHISTORYVIEW_ALLSTOCKS_TAB );
		stocklistTab.setClosable(false);
		this.getTabs().add( stocklistTab );
		
		addStockListListeners();
	}
	
	private void createWatchListTab() {
		watchlistListView = new ListView<ListedStockTO>();
		
		watchlistTab = new Tab();
		watchlistTab.setContent( watchlistListView );
		watchlistTab.setText( StringConstants.PRICEHISTORYVIEW_WATCHLIST_TAB );
		watchlistTab.setClosable(false);
		this.getTabs().add( watchlistTab );
		
		addWatchListListeners();
	}
	
	private void addTabListener() {
		this.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
			if ( oldTab != null ) {
				if ( oldTab.getContent() instanceof ListView ) {
					ListView listView = (ListView)oldTab.getContent();
					listView.getSelectionModel().clearSelection();
				}
			}
		});
	}
	
	private void addStockListListeners() {
		stocklistListView.setCellFactory( factory -> {
			ListCell<ListedStockTO> cell = new ListCell();
			
			cell.textProperty().bind(cell.itemProperty().asString());
			
			ContextMenu contextMenu = new ContextMenu();
			MenuItem addToWatchlistItem = new MenuItem();
			addToWatchlistItem.setText( StringConstants.PRICEHISTORYVIEW_ADDTOWATCHLIST );
			addToWatchlistItem.setOnAction( event -> {
				watchlistListView.getItems().add( cell.getItem() );
				addToWatchListAction( cell.getItem() );
			} );
			contextMenu.getItems().add( addToWatchlistItem );
			
			cell.emptyProperty().addListener( (observableValue, oldIsEmpty, newIsEmpty) -> {
	            if ( newIsEmpty ) {
	            	cell.setContextMenu( null );
	            } else {
	                cell.setContextMenu( contextMenu );
	            }
	        });
			
			cell.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});
			
			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});
			
			return cell;
		});
		
		stocklistListView.getSelectionModel().selectedItemProperty().addListener( new StockSelectionListener() );
	}

	private void addToWatchListAction( ListedStockTO p_stockTO ) {
		controller.updateWatchlistStatus( p_stockTO, true);
	}

	private void removeFromWatchListAction( ListedStockTO p_stockTO ) {
		controller.updateWatchlistStatus( p_stockTO, false);
	}
	
	private void addWatchListListeners() {
		watchlistListView.setCellFactory( factory -> {
			ListCell<ListedStockTO> cell = new ListCell();

			cell.textProperty().bind(cell.itemProperty().asString());

			ContextMenu contextMenu = new ContextMenu();
			MenuItem removeFromWatchlistItem = new MenuItem();
			removeFromWatchlistItem.setText( "Remove from watchlist" );
			removeFromWatchlistItem.setOnAction( event -> {
				removeFromWatchListAction( cell.getItem() );
				watchlistListView.getItems().remove( cell.getItem() );
			} );
			contextMenu.getItems().add( removeFromWatchlistItem );

			cell.emptyProperty().addListener( (observableValue, oldIsEmpty, newIsEmpty) -> {
				if ( newIsEmpty ) {
					cell.setContextMenu( null );
				} else {
					cell.setContextMenu( contextMenu );
				}
			});

			cell.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});

			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				if ( e.getButton() == MouseButton.SECONDARY ) {
					e.consume();
				}
			});

			return cell;
		});

		watchlistListView.getSelectionModel().selectedItemProperty().addListener( new StockSelectionListener() );
	}
	
	private class StockSelectionListener implements ChangeListener<ListedStockTO>{
		@Override
		public void changed(ObservableValue<? extends ListedStockTO> observable, ListedStockTO oldValue, ListedStockTO newValue) {
			if ( newValue != null ) {
				controller.selectStock( newValue.getTicker() );
			}
		}
	}
	
}
