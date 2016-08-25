package priceHistory;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import priceHistory.dataFeed.DataFeedTO;

public class PriceChart {
	
	private LineChart chart;
	
	public PriceChart( ObservableList<DataFeedTO> p_priceHistory ) {
		
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		
		final LineChart<Number, Number> linechart = new LineChart<Number,Number>(xAxis,yAxis);
		
		XYChart.Series series = new XYChart.Series();
		
		series.getData().addAll(p_priceHistory);
		
		linechart.getData().add(series);
		setChart(linechart);
		
	}

	public LineChart getChart() {
		return chart;
	}

	public void setChart(LineChart chart) {
		this.chart = chart;
	}

}
