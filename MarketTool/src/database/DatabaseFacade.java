package database;

import java.sql.SQLException;

import priceHistory.DefaultFacade;

public class DatabaseFacade extends DefaultFacade {
	
	public void initialiseDatabase() {
		PoolableConnection connection = getDatabaseConnection();
		
		connection.beginTransaction();
		
		DatabaseService databaseService = new DatabaseService();
		try {
			databaseService.createTables( connection );
			databaseService.insertInitialData( connection );
			
			connection.commitTransaction();
		} catch (SQLException e) {
			throw new RuntimeException();
		} finally {
			connection.returnToPool();
		}
		
	}
	
	public void eraseAllData() {
		PoolableConnection connection = getDatabaseConnection();
		
		connection.beginTransaction();
		
		DatabaseService databaseService = new DatabaseService();
		try {
			databaseService.createTables( connection );
			
			connection.commitTransaction();
		} catch (SQLException e) {
			throw new RuntimeException();
		} finally {
			connection.returnToPool();
		}
	}
	
}
