package com.keysight.database.helpers;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

// see http://www.kfu.com/~nsayer/Java/dyn-jdbc.html

public class DatabaseDriverDelegate implements Driver {
	private Driver driver;
	public DatabaseDriverDelegate(Driver d) {
		this.driver = d;
	}

	public boolean acceptsURL(String u) throws SQLException, SQLFeatureNotSupportedException {
		return this.driver.acceptsURL(u);
	}
	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}
    public Logger getParentLogger() throws SQLFeatureNotSupportedException { 
        return this.driver.getParentLogger();
    }
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}
}
 
