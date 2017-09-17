package database.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
	
	private Connection connection;
	
	public SQLiteConnection(){
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:sqliteDB.db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
