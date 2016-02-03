package com.eha.grits.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class FlightLegDAOJDBCImpl implements FlightLegDAO{
	
	/**
	 * Search legs by departure airport, and greater than or equal number of seats, and effective date range OVERLAPS with search criteria.
	 */
	@Override
	public List<FlightLeg> searchLegsByDeparture(LocalDate startDate, 
											LocalDate endDate, 
											int totalSeats,
											String departureAirportCode) {
										
		List<FlightLeg> legs = new ArrayList<FlightLeg>();
		Connection conn =  null;
		try {
			   conn = ConnectionFactory.getConnection();
			   
			   //Check for legs that have scheduleded overlap time with the window of time between startDate and endDate
			   //to check for overlap: WHERE new_start < existing_end AND new_end   > existing_start;
			   
			   PreparedStatement ps = 
					
					 conn.prepareStatement( "SELECT * FROM legs WHERE departureAirportCode = ? AND ? < discontinuedDate AND ? > effectiveDate AND totalSeats > ?");
			   	 
			   		ps.setString(1,  departureAirportCode );
			   		ps.setDate(2,  java.sql.Date.valueOf(startDate) );
			   		ps.setDate(3,  java.sql.Date.valueOf(endDate) );
			   		ps.setInt(4,  totalSeats );
			   		ResultSet rs = ps.executeQuery();
			   		
					while (rs.next()) {
						String _flightID 				= rs.getString("flightID");
						String _departureAirportCode 	= rs.getString("departureAirportCode");
						Double _departureAirportLat		= rs.getDouble("departureAirportLat");
						Double _departureAirportLng		= rs.getDouble("departureAirportLng");
						
						String _arrivalAirportCode 		= rs.getString("arrivalAirportCode");
						Double _arrivalAirportLat		= rs.getDouble("arrivalAirportLat");
						Double _arrivalAirportLng		= rs.getDouble("arrivalAirportLng");
						
						LocalDate _effectiveDate 		= rs.getDate("effectiveDate").toLocalDate();
						LocalDate _discontinuedDate 	= rs.getDate("discontinuedDate").toLocalDate();
						LocalTime _departureTimeUTC 	= rs.getTime("departureTimeUTC").toLocalTime();
						LocalTime _arrivalTimeUTC	 	= rs.getTime("arrivalTimeUTC").toLocalTime();
						Boolean _day1					= rs.getBoolean("day1");
						Boolean _day2					= rs.getBoolean("day2");
						Boolean _day3					= rs.getBoolean("day3");
						Boolean _day4					= rs.getBoolean("day4");
						Boolean _day5					= rs.getBoolean("day5");
						Boolean _day6					= rs.getBoolean("day6");
						Boolean _day7					= rs.getBoolean("day7");
						Integer	_weeklyFrequency		= rs.getInt("weeklyFrequency");
						Integer	_totalSeats				= rs.getInt("totalSeats");
						
						legs.add(new FlightLeg(_flightID, 
								_departureAirportCode, _departureAirportLat, _departureAirportLng,
								_arrivalAirportCode, _arrivalAirportLat, _arrivalAirportLng,
								_effectiveDate, 
								_discontinuedDate, 
								_departureTimeUTC, 
								_arrivalTimeUTC,
								_day1, _day2, _day3, _day4, _day5, _day6, _day7,
								_weeklyFrequency, 
								_totalSeats								
						));					
					}			   	 
		}
		catch( SQLException e ) {
			System.out.println(e);
		}
		finally {
			try {
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex1) {
	        	ex1.printStackTrace();
	        }
		 } 
		return legs;
	}
	 
	@Override
	public int create(FlightLeg leg) {
		int result = 0;
		Connection conn =  null;
		try {
			   conn = ConnectionFactory.getConnection();
			   PreparedStatement ps = 
			     conn.prepareStatement( "INSERT INTO legs"
			     		+ " (flightID, departureAirportCode, departureAirportLat, departureAirportLng, arrivalAirportCode, arrivalAirportLat, arrivalAirportLng, effectiveDate, discontinuedDate,"
			     		+ " departureTimeUTC,"
			     		+ " arrivalTimeUTC, day1, day2, day3, day4, day5, day6, day7, weeklyFrequency, totalSeats)"
			     		+ " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )" );
			   
			   ps.setString( 1, leg.getFlightID() );
			   ps.setString( 2, leg.getDepartureAirportCode() );
			   ps.setDouble( 3, leg.getDepartureAirportLat() );
			   ps.setDouble( 4, leg.getDepartureAirportLng() );
			   
			   ps.setString( 5, leg.getArrivalAirportCode() );
			   ps.setDouble( 6, leg.getArrivalAirportLat() );
			   ps.setDouble( 7, leg.getArrivalAirportLng() );
			   
			   ps.setDate( 8,  java.sql.Date.valueOf( leg.getEffectiveDate() ));
			   ps.setDate( 9,  java.sql.Date.valueOf( leg.getDiscontinuedDate() ));
			   ps.setTime( 10,  java.sql.Time.valueOf( leg.getDepartureTimeUTC() ));
			   ps.setTime( 11,  java.sql.Time.valueOf( leg.getArrivalTimeUTC() ));
			   ps.setBoolean( 12, leg.isDay1() );
			   ps.setBoolean( 13, leg.isDay2() );
			   ps.setBoolean( 14, leg.isDay3() );
			   ps.setBoolean( 15, leg.isDay4() );
			   ps.setBoolean( 16, leg.isDay5() );
			   ps.setBoolean( 17, leg.isDay6() );
			   ps.setBoolean( 18, leg.isDay7() );
			   
			   ps.setInt(19, leg.getWeeklyFrequency() );
			   ps.setInt(20, leg.getTotalSeats() );
	   
			   System.out.println( ps.toString() );
			   result = ps.executeUpdate();
		}
		catch( SQLException e ) {
			System.out.println(e);
		}
		finally {
			try {
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex1) {
	        	ex1.printStackTrace();
	        }
		 } 
		return result;
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(long id, FlightLeg model) {
		// TODO Auto-generated method stub
		
	}
 



}
