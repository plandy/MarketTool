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
			e.printStackTrace();
		}
		
	}
	
	public void returnConnection( PoolableConnection p_connection ) {
		connectionPool.add(p_connection);
	}
	
}
