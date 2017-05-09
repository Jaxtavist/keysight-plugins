package com.keysight.database.helpers;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

// This code is 98% copied from 
// http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
//
// On 5/8/2017, Nick Sayer sent me an email declaring
// the code to be in the public domain and that I may
// use his code in my project and distribute it under
// an Apache 2 license.

public class DatabaseDriverDelegate implements Driver {
	private Driver driver;
	public DatabaseDriverDelegate(Driver driver) {
        setRealDriver( driver );
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

    public Driver getRealDriver(){
       return this.driver;
    }
    public void setRealDriver( Driver driver ){
       this.driver = driver;
    }
}
 
