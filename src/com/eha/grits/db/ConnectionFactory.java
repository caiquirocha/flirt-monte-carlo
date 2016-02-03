package com.eha.grits.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Main legs database, mysql for now
 * @author brocka
 *
 */
public class ConnectionFactory {
    
	private static final BasicDataSource dataSource = new BasicDataSource();

	static {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/flirt_legs?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("MTM1mlrm");
    }

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
 
}
