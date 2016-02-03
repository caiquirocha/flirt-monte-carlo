package com.eha.grits.db;

import java.time.LocalDate;
import java.util.List;

/**
 * This represents a scheduled FlightLeg, taken from published flight data. 
 * It does not represent a specific flight in time however.
 * 
 * @author brocka
 *
 */

public interface FlightLegDAO {
	 
	public int 	create( FlightLeg leg );
	public void delete( long id );
	public void update( long id, FlightLeg leg );
	
	public List<FlightLeg> searchLegsByDeparture( LocalDate effectiveDate, LocalDate discontinuedDate, int totalSeats, String airportDepartureCode );
	
}
