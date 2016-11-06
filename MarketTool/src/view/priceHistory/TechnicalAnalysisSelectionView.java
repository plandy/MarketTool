package view.priceHistory;

import java.util.ArrayList;

import applicationConstants.StringConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import priceHistory.PriceHistoryController;

public class TechnicalAnalysisSelectionView extends ListView<CheckBox> {
	
	PriceHistoryController controller;
	
	private static final int[] numDays = { 10, 20, 50, 100, 200 };
	
	public TechnicalAnalysisSelectionView( PriceHistoryController p_controller ) {
		super();
		
		controller = p_controller;
		
		this.setItems( createCheckboxList() );
	}
	
	private ObservableList<CheckBox> createCheckboxList() {
		ArrayList<CheckBox> arrayList = new ArrayList<CheckBox>();
		
		for ( int day : numDays ) {
			arrayList.add( createEMAcheckbox(day) );
		}
		
		return FXCollections.observableArrayList( arrayList );
	}
	
	public CheckBox createEMAcheckbox( int p_numDays ) {
		CheckBox checkBox = new CheckBox( StringConstants.EXPONENTIALMOVINGAVERAGE_DAYS + p_numDays );
		checkBox.selectedProperty().addListener( new NotifyEMA_Listener(p_numDays, controller) );
		
		return checkBox;
	}
	
	private class NotifyEMA_Listener implements ChangeListener<Boolean> {
		PriceHistoryController l_controller;
		private int l_numDays;
		
		public NotifyEMA_Listener( int p_numDays, PriceHistoryController p_controller ) {
			l_controller = p_controller;
			l_numDays = p_numDays;
		}

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			l_controller.notify_EMA( newValue, l_numDays );
		}
	}
}
