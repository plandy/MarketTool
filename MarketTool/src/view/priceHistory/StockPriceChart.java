package view.priceHistory;

import java.util.Date;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import view.javaFXChart.DateAxis;

public class StockPriceChart extends LineChart<Date,Number> {
	
	private DateAxis xDateAxis;
	private NumberAxis yPriceAxis;
	
	public StockPriceChart() {
		super( new DateAxis(), new NumberAxis() );
		
		xDateAxis = (DateAxis) super.getXAxis();
		yPriceAxis = (NumberAxis) super.getYAxis();
		
		setDefaultStyle();
	}
	
	private void setDefaultStyle() {
		yPriceAxis.setForceZeroInRange(false);
		
		this.setAnimated(false);
		this.setLegendVisible(false);
		this.setCreateSymbols(false);
		this.setHorizontalGridLinesVisible(true);
		this.setVerticalGridLinesVisible(true);
	}
}
