package priceHistory;

import database.ConnectionManager;
import database.PoolableConnection;

public class DefaultFacade {
	
	protected PoolableConnection getDatabaseConnection() {
		return ConnectionManager.INSTANCE.getConnection();
	}

}
