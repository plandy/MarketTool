package database;

public enum ConnectionManager {
	
	INSTANCE;
	
	private final ConnectionPool connectionPool = new ConnectionPool( 1, 3 );
	
	public PoolableConnection getConnection() {
		try {
			return connectionPool.requestConnection();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

}
