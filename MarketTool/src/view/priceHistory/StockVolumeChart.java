package view.priceHistory;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

public class StockVolumeChart extends BarChart<String,Number> {
	
	private CategoryAxis xDateAxis;
    private NumberAxis yPriceAxis ;

	public StockVolumeChart() {
		super( new CategoryAxis(), new NumberAxis() );
		
		xDateAxis = (CategoryAxis) super.getXAxis();
		yPriceAxis = (NumberAxis) super.getYAxis();
		
		SetDefaultStyle();
	}
	
	private void SetDefaultStyle() {
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
