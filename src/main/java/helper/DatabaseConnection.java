package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exceptions.DatabaseExceptions;


public class DatabaseConnection {

	static Connection conn = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnection.class);
	private static DatabaseConnection dbcon;

	private DatabaseConnection() {
	}

	public static DatabaseConnection getInstance() {
		if (dbcon == null) {
			dbcon = new DatabaseConnection();
		}
		return dbcon;
	}

	public void createDBConnection(String dbURL, String className, String username, String password) throws DatabaseExceptions{
		try {
			Class.forName(className);
			LOG.info("connection started");
			conn = DriverManager.getConnection(dbURL, username, password);
			LOG.info("connection created");
		} catch (ClassNotFoundException | SQLException e) {
			LOG.error(e.getMessage());
			throw new DatabaseExceptions("&ensp;&ensp; Connection not created <br />" + e.getMessage());
		}
	}

	public void closeDBConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public static synchronized Connection getCon() {
		return conn;
	}
}
