package database;

public enum ConnectionManager {
	
	INSTANCE;
	
	private final ConnectionPool connectionPool = new ConnectionPool( 3 );
	
	public PoolableConnection getConnection() {
		return connectionPool.getConnection();
	}

}
