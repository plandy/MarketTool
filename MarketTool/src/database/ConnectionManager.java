package database;

public enum ConnectionManager {
	
	INSTANCE;

	private static final int SQLITE_BUSY_CAPACITY = 1;
	private static final int NUMBER_I_DONT_HATE = 5;

	private final ConnectionPool connectionPool = new ConnectionPool( NUMBER_I_DONT_HATE );
	
	public PoolableConnection getConnection() {
		return connectionPool.getConnectionSpinWait();
	}

}
