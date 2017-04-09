package database;

public enum ConnectionManager {
	
	INSTANCE;

	private static final int SQLITE_BUSY_CAPACITY = 1;

	private final ConnectionPool connectionPool = new ConnectionPool( SQLITE_BUSY_CAPACITY );
	
	public PoolableConnection getConnection() {
		return connectionPool.getConnectionSpinWait();
	}

}
