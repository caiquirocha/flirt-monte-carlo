package com.eha.grits.util;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import com.eha.grits.db.ConnectionFactory;
 

/**
* This class has some handy utils for quickly recreating the legs database and tables. 
* I used this during development to quickly create/recrete the database
* 
*/

public class DBUtil {
	
	 /**
	  * Create the legs table, this table is the flights mongo collection all broken out into individual legs
	  * 
	  * 
	  * http://mysql.rjweb.org/doc.php/latlng
	  * 
     * @throws SQLException
     */
    public static void createFlightLegTable() throws SQLException {
    	           
    	Connection connection = null;
    	Statement statement = null;
    	
        String sql = "CREATE TABLE legs " +
                "(id MEDIUMINT NOT NULL AUTO_INCREMENT, " +
                " flightID VARCHAR(32), " + 
                " departureAirportCode VARCHAR(3), " + 
				" departureAirportLat DECIMAL(8,6), " +
				" departureAirportLng DECIMAL(9,6), " +
                " arrivalAirportCode VARCHAR(3), " + 
                " arrivalAirportLat DECIMAL(8,6), " +
                " arrivalAirportLng DECIMAL(9,6), " +
                " effectiveDate DATE, " + 
                " discontinuedDate DATE, " + 
                " departureTimeUTC TIME, " + 
                " arrivalTimeUTC TIME, " + 
                " day1 BOOL," +
                " day2 BOOL," +
                " day3 BOOL," +
                " day4 BOOL," +
                " day5 BOOL," +
                " day6 BOOL," +
                " day7 BOOL," +
                " weeklyFrequency INTEGER," +
                " totalSeats INTEGER," +
                " PRIMARY KEY ( id )," + 
                " INDEX (departureAirportCode), " + 
        		" INDEX (arrivalAirportCode), " +
        		" INDEX (effectiveDate), " + 
                " INDEX (discontinuedDate)) "; 

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);                
            SQLWarning warning = statement.getWarnings();

            if (warning != null)
                throw new SQLException(warning.getMessage());
        
        } catch (SQLException e) {
            throw e;
        } finally {
            DBUtil.close(statement);
            DBUtil.close(connection);
        }
    }
    
    
    public static void dropTable() {
    	Connection connection = null;
    	Statement statement = null;
    	
        String query = "DROP TABLE legs";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
            
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            DBUtil.close(statement);
            DBUtil.close(connection);
        }
    }
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                //TODO Add LOG4J 
            }
        }
    }
 
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                //TODO Add LOG4J 
            }
        }
    }
 
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                //TODO Add LOG4J 
            }
        }
    }
}
