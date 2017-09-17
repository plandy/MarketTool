package view.priceHistory;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import view.IUIControl;

public class StockVolumeChart extends BarChart<String,Number> implements IUIControl {
	
	final private CategoryAxis xDateAxis;
    final private NumberAxis yPriceAxis;

	public StockVolumeChart() {
		super( new CategoryAxis(), new NumberAxis() );
		
		xDateAxis = (CategoryAxis) super.getXAxis();
		yPriceAxis = (NumberAxis) super.getYAxis();
		
		setDefaultStyle();
	}
	
	@Override
	public void setDefaultStyle() {
		xDateAxis.setTickLabelsVisible(false);
		xDateAxis.setTickMarkVisible(false);
		
        yPriceAxis.setForceZeroInRange(false);
        yPriceAxis.setTickLabelsVisible(true);
        yPriceAxis.setTickMarkVisible(true);
        yPriceAxis.setMinorTickVisible(false);
        
        this.setAnimated(false);
        this.setLegendVisible(false);
        this.setHorizontalGridLinesVisible(false);
        this.setVerticalGridLinesVisible(false);
	}

}
