package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
	
	private BlockingQueue<PoolableConnection> connectionPool;
	
	private int maxPoolSize;
	AtomicInteger currentPoolSize; 
	
	public ConnectionPool( int p_initialPoolSize, int p_maxPoolSize ) {
		if ( p_maxPoolSize < p_initialPoolSize || p_initialPoolSize < 0 || p_maxPoolSize < 1 ) {
			throw new IllegalArgumentException("blarg");
		}
		currentPoolSize = new AtomicInteger(0);
		connectionPool = new LinkedBlockingQueue<PoolableConnection>(p_maxPoolSize);		
		
		for ( int count = 0; count < p_initialPoolSize; count++ ) {
			addConnection();
		}
	}
	
	/**
	 * Request a connection from the pool.
	 * <p>
	 * If an idling connection exists, return this. If no idle connection, the pool creates a new connection and returns it.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	//TODO handle Case: no idle connections and pool already at max capacity.
	public PoolableConnection requestConnection() throws InterruptedException {
		
		PoolableConnection connection = null;
		
		synchronized( connectionPool ) {
			if ( connectionPool.peek() != null ) {
				connection = connectionPool.take();
			} else {
				if ( currentPoolSize.get() < maxPoolSize ) {
					addConnection();
					connection = connectionPool.take();
				}
			}
		}
		return connection;
	}
	
	private void addConnection() {
		try {
			Connection l_connection = DriverManager.getConnection("jdbc:sqlite:marketToolDB.db");
			connectionPool.offer( new PoolableConnection( l_connection, this ) );
			currentPoolSize.incrementAndGet();
		} catch (SQLException e) {
			throw new RuntimeException("error creating database connection");
		}
		
	}
	
	/**
	 * Return the connection to this pool.
	 * <br>
	 * Should only be called from a connection, and should have package access only or any connection could be returned to any pool.
	 * 
	 * @param p_connection the connection to return.
	 */
	void returnConnection( PoolableConnection p_connection ) {
		connectionPool.add(p_connection);
	}
	
	public void closeConnection ( PoolableConnection p_connection ) {
		p_connection.destroy();
		currentPoolSize.decrementAndGet();
	}
	
}
