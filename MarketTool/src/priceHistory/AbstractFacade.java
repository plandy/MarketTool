package priceHistory;

import database.ConnectionManager;
import database.PoolableConnection;

public abstract class AbstractFacade {
	
	protected PoolableConnection getDatabaseConnection() {
		return ConnectionManager.INSTANCE.getConnection();
	}

}
