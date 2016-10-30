package technicalAnalysis;

import java.util.List;

import priceHistory.dataFeed.DataFeedTO;

public class TechnicalAnalysisFacade {
	
	TechnicalAnalysisService service;
	
	public TechnicalAnalysisFacade() {
		service = new TechnicalAnalysisService();
	}
	
	public void calculateSimpleMovingAverage_10Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 10;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_20Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 20;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_50Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 50;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_100Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 100;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateSimpleMovingAverage_200Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 200;
		service.calculateSimpleMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_10Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 10;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_20Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 20;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_50Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 50;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_100Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 100;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
	
	public void calculateExponentialMovingAverage_200Day( List<DataFeedTO> p_priceHistory ) {
		int numDays = 200;
		service.calculateExponentialMovingAverage( numDays, p_priceHistory );
	}
}
