package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionPool {
	
	private final PoolableConnection[] pool;
	private final AtomicLong counter;
	private final long INITIAL_COUNTER_VALUE = 0L;
	private static final long EMPTY_OWNER_VALUE = -1L;
	
	public ConnectionPool( int p_capacity ) {
		
		counter = new AtomicLong( INITIAL_COUNTER_VALUE );
		
		pool = new PoolableConnection[p_capacity];
		
		for ( int i = 0; i < p_capacity; i++ ) {
			pool[i] = createPoolableConnection();
		}
	}
	
	public PoolableConnection getConnection() {
		
		PoolableConnection returnConnection = null;
		
		long ticket = counter.getAndIncrement();
		boolean success = false;
		
		while ( success == false ) {
			for ( PoolableConnection connection : pool ) {
				if ( connection.compareAndSet(ticket) ) {
					returnConnection = connection;
					success = true;
					break;
				}
			}
		}
		
		return returnConnection;
		
	}

	private PoolableConnection createPoolableConnection() {
		Connection connection;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:marketToolDB.db");
		} catch (SQLException e) {
			throw new RuntimeException("error creating database connection");
		}
		
		return new PoolableConnection( connection );
	}
	
}
