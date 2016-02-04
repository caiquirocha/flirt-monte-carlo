package com.eha.grits;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.db.FlightLegDAO;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.flow.FlightLegInstance;
import com.eha.grits.flow.Passenger;
import com.eha.grits.tools.TimeUtil;
import com.eha.grits.util.DistributedRandomNumberGenerator;

public class AirportFlow {

 

	 /** 
	 * Main Entry Point
	 * @param args
	 */
	public static void main(String[] args) {
		
		RandomDataGenerator randomData = new RandomDataGenerator(); 
		 
		//These will be configurable dates
		LocalDate 	startDate 		= LocalDate.now().minus(17, ChronoUnit.DAYS);		
		LocalDate 	endDate 		= LocalDate.now().plus(1, ChronoUnit.DAYS);
		Integer		totalSeatsMin	= 0;
		Integer 	maxLegs			= 10;
		
		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
		
		//Search legs by departure airport, and greater than or equal number of seats, and effective date range OVERLAPS with search criteria.
		List<FlightLeg> scheduledLegs = legDAO.searchLegsByDeparture(startDate, endDate, totalSeatsMin, "JFK");		
		
		//Convert scheduled legs into actual leg instances inside the start/end time window
		List<FlightLegInstance> legInstances = getLegsWithinTimePeriod(startDate, endDate, scheduledLegs);

		//Create random number of leg picker based on current distribution
		DistributedRandomNumberGenerator numberLegsPicker = setupDistributedNumberGenerator( maxLegs );
		
		//Create new Passenger and simulate a trip!
		//TODO Repeat me MANY MANY times.
		Passenger p = new Passenger();
		p = simulateTrip( p, numberLegsPicker.getDistributedRandomNumber(), legInstances, Duration.ofHours(8) );
		
				
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

	private static Passenger simulateTrip(Passenger p, int numberLegs, List<FlightLegInstance> legInstances, Duration maxLayOverTime) {
		
		if( p.getLegs().size() == 0 ){
			//Just starting trip, all FlightLegInstances are on the table
			p.addLeg( pickStartingLeg( legInstances ) );
		} else{			
			//Check out last leg, pick from available legs
			FlightLegInstance lastLeg = p.getLegs().get( p.getLegs().size() - 1);
			
			LocalTime 	arrival 		= lastLeg.getArrivalTimeUTC();
			String 		arrivalAirport	= lastLeg.getArrivalAirportCode();
			
			//TODO Get leg instances from next airport and pick next flight leg
			//FEB 3 11:30 PM RIGHT HERE
			//List<FlightLeg> scheduledLegs = legDAO.searchLegsByDeparture(startDate, endDate, totalSeatsMin, "JFK");		
		}
		
		if(p.getLegs().size() < numberLegs)
			p = simulateTrip(p, numberLegs, legInstances, maxLayOverTime);

		return p;
	}
	
	private static FlightLegInstance getNextLeg(List<FlightLegInstance> legInstances, FlightLegInstance curLeg, Duration maxLayOverTime  ) {
		FlightLegInstance nextLeg = null;
		
		return nextLeg;
	}
	
	/**
	 * Pick a random leg from entire leg collection. java.util.Random should be a uniform distribution.
	 * 
	 * @param legInstances
	 * @return
	 */
	private static FlightLegInstance pickStartingLeg(List<FlightLegInstance> legInstances) {
		
		//Probably should initialize the random generator outside of this block
		return legInstances.get(new Random().nextInt(legInstances.size()));
	}
	
	private static List<FlightLegInstance> getLegsWithinLayoverTime(List<FlightLegInstance> allLegs, FlightLegInstance curLeg, Duration maxLayOverTime) {
		
		List<FlightLegInstance> result = new ArrayList<FlightLegInstance>();		
		for(FlightLegInstance leg : allLegs){
			if( TimeUtil.getInstance().isLayoverCompatible(curLeg.getArrivalTimeUTC(), leg.getDepartureTimeUTC(), maxLayOverTime.toMinutes()) ) {
				result.add(leg);
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
				if (!date.isBefore(leg.getEffectiveDate()) && !date.isAfter(leg.getDiscontinuedDate()) && TimeUtil.getInstance().fliesThisDay(leg, date) == true) {
					flights.add( new FlightLegInstance(leg, date));
				}				
			}
		}	
		return flights;
	}
	
	private static DistributedRandomNumberGenerator setupDistributedNumberGenerator(Integer maxLegs) {
		DistributedRandomNumberGenerator numberLegsPicker = new DistributedRandomNumberGenerator();
        
		List<Double> distribution = new ArrayList<Double>();
		distribution.add(0.6772732d);
		distribution.add(0.2997706d);
        distribution.add(0.0211374d);
        distribution.add(0.0016254d);
        distribution.add(0.0001632d);
        distribution.add(0.0000215d);
        distribution.add(0.0000072d);
        distribution.add(0.0000012d);
        distribution.add(0.0000002d);
        distribution.add(0.0000001d);
		
        for(int i=0; i < maxLegs; i++){
        	numberLegsPicker.addNumber(i+1, distribution.get(i));
        }
        return numberLegsPicker;
	}

}
