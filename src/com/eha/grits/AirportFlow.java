package com.eha.grits;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.db.FlightLegDAO;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.flow.FlightLegInstance;
import com.eha.grits.flow.Passenger;

public class AirportFlow {

	
	/** 
	 * Main Entry Point
	 * @param args
	 */
	public static void main(String[] args) {
		
		//These will be configurable dates
		LocalDate 	startDate 		= LocalDate.now().minus(17, ChronoUnit.DAYS);		
		LocalDate 	endDate 		= LocalDate.now().plus(1, ChronoUnit.DAYS);
		Integer		totalSeatsMin	= 0;
		
		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
		
		//Search legs by departure airport, and greater than or equal number of seats, and effective date range OVERLAPS with search criteria.
		List<FlightLeg> legs = legDAO.searchLegsByDeparture(startDate, endDate, totalSeatsMin, "JFK");		
		
		//Convert scheduled legs into actual leg instances inside the start/end time window
		List<FlightLegInstance> filteredLegs = getLegsWithinTimePeriod(startDate, endDate, legs);
		
		//FEB 3 - WE ARE RIGHT HERE.
		
		//just checking output
		for(FlightLegInstance leg : filteredLegs ) {
			System.out.println(leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode());
		}
		
				
		/**
		 * Simulate passenger, make recursive method on this class
		 * take an origin for the passenger
		 * number of legs traveled so far, appened them to list of legs traveled
		 * 
		 * get the searchLegsByDeparture airport logic in that recursive function
		 * 
		 * probability mass function
		 * returning the value of the probabilty mass function for teh time of each flight
		 * returns value of 1 or some fraction of total flights
		 */	
	}
	
	//Monte Carlo simulation
	private static Passenger simulateTrip(Passenger p, List<FlightLegInstance> filteredLegs, Duration maxLayOverTime) {
		
		//Just starting trip, all flightleginstances are on the table
		if(p.getLegs().size() == 0){
			
		} else {			
			FlightLegInstance lastLeg = p.getLegs().get( p.getLegs().size() - 1);
			LocalTime arrival = lastLeg.getArrivalTimeUTC();
			
		}
		return p;
	}
	
	private static FlightLegInstance getNextLeg(List<FlightLegInstance> legs, FlightLegInstance curLeg, Duration maxLayOverTime  ) {
		FlightLegInstance nextLeg = null;
		
		return nextLeg;
	}
	

	private static List<FlightLegInstance> getLegsWithinLayoverTime(List<FlightLegInstance> legs, FlightLegInstance curLeg, Duration maxLayOverTime) {
		
		List<FlightLegInstance> result = new ArrayList<FlightLegInstance>();		
				
		//Absolutely Last Possible Departure Time. CAREFUL - could be past current midnight, could easily be early next morning:
		LocalTime lastPossibleDeparture = curLeg.getArrivalTimeUTC().plus(maxLayOverTime);
		
		/**
		 * 
		 * Find all the legs within the alloted maxLayoverTime
		 * 
		 * Need to pay special attention to the midnight boundary here.
		 * 
		 */
		
		for(FlightLeg leg : legs){
		
			if(curLeg.getArrivalTimeUTC().plus(maxLayOverTime).isBefore(curLeg.getArrivalTimeUTC() ) ) {
				//we tripped over the midnight boundary
								
			}
			
		}	
		
		return result;
	}
	
	/**
	 * Find all legs within collection that are inside startDate/endDate window, and fly on appropriate day of week.
	 * @param startDate
	 * @param endDate
	 * @param legs
	 * @return
	 */
	private static List<FlightLegInstance> getLegsWithinTimePeriod(LocalDate startDate, LocalDate endDate, List<FlightLeg> legs) {
		
		List<FlightLegInstance> flights = new ArrayList<FlightLegInstance>();		
		for(FlightLeg leg : legs){
			for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
			    // This date is in range of the effective and discontinued dates of this leg. Add a flightInstance!					
				if (!date.isBefore(leg.getEffectiveDate()) && !date.isAfter(leg.getDiscontinuedDate()) && fliesThisDay(leg, date) == true) {
					flights.add( new FlightLegInstance(leg, date));
				}				
			}
		}	
		return flights;
	}
	
	/**
	 * See if this leg flies on this day of the week
	 * @param leg
	 * @param date
	 * @return
	 */
	private static boolean fliesThisDay(FlightLeg leg, LocalDate date) {
		boolean result = false;
		
		switch (date.getDayOfWeek().getValue()) {
	        case 1:  
	     		if( leg.isDay1() )
	      			result = true;
	            break;
	        case 2:  
	     		if( leg.isDay2() )
	      			result = true;
	            break;
	        case 3:  
	     		if( leg.isDay3() )
	      			result = true;
	            break;
	        case 4:  
	     		if( leg.isDay4() )
	      			result = true;
	            break;
	        case 5:  
	     		if( leg.isDay5() )
	      			result = true;
	            break;
	        case 6:  
	     		if( leg.isDay6() )
	      			result = true;
	            break;
	        case 7:  
	     		if( leg.isDay7() )
	      			result = true;
	            break;
	        default: 
	        	result = false;
		}
		return result;	
	}

}
