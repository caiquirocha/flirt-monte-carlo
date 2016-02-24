package com.eha.grits;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.flow.FlightLegInstance;
import com.eha.grits.flow.Passenger;
import com.eha.grits.tools.TimeUtil;
import com.eha.grits.util.DistributedRandomNumberGenerator;

public class AirportFlow {

	 /** 
	 * We are using the python simulation service AirportFlowCalculator.py instead.
	 * 
	 * @param args
	 */
	
	@Deprecated
	public static void main(String[] args) {
		
		//These will be configurable dates
		LocalDate 	startDate 		= LocalDate.now().minus(17, ChronoUnit.DAYS);		
		LocalDate 	endDate 		= LocalDate.now().plus(1, ChronoUnit.DAYS);
		Integer		totalSeatsMin	= 0;
		Integer 	maxLegs			= 10;
		String 		startingAirport	= "JFK";
		
		//Create random number of leg picker based on current distribution
		DistributedRandomNumberGenerator numberLegsPicker = setupDistributedNumberGenerator( maxLegs );
				    
		for(int i=0; i< 1000; i++) {
			Passenger p = new Passenger();
			p = simulateTrip( p, numberLegsPicker.getDistributedRandomNumber(), Duration.ofHours(8), startDate, endDate, totalSeatsMin, startingAirport );
		}

	}

	private static Passenger simulateTrip(Passenger p, int numberLegs, Duration maxLayOverTime, LocalDate startDate, LocalDate endDate, Integer totalSeatsMin, String startingAirport) {
		
		if( p.getLegs().size() == 0 ){
			//Just starting trip, all FlightLegInstances are on the table
			p.addLeg( pickStartingLeg( startDate, endDate, totalSeatsMin, startingAirport  ) );
			
			FlightLegInstance leg = p.getLegs().get(0);			
			System.out.println("TripStart: " + leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " on " + leg.getInstanceDate() + " at " + leg.getDepartureTimeUTC() );
			if(p.getLegs().size() < numberLegs) {
				p = simulateTrip(p, numberLegs, maxLayOverTime, startDate, endDate, totalSeatsMin, startingAirport );
			}
		} else{			
			//Check out last leg, pick from available legs
			FlightLegInstance lastLeg = p.getLegs().get( p.getLegs().size() - 1);
			//int size = p.getLegs().size();
			
			if(lastLeg == null){
				System.out.println("Why is this null");
			}
			LocalDate	arrivalDate			= lastLeg.getInstanceDate();
			LocalTime 	arrivalTime 		= lastLeg.getArrivalTimeUTC();
			String 		arrivalAirport		= lastLeg.getArrivalAirportCode();
			
			FlightLegInstance leg = getNextLeg( maxLayOverTime, totalSeatsMin, arrivalDate, arrivalTime, arrivalAirport );
			if(leg != null) {
				p.addLeg(leg);
				System.out.println("AddedLeg: " + leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " on " + leg.getInstanceDate() + " at " + leg.getDepartureTimeUTC() );
				if(p.getLegs().size() < numberLegs) {
					p = simulateTrip(p, numberLegs, maxLayOverTime, startDate, endDate, totalSeatsMin, startingAirport );
				}
			}
		}	
		return p;
	}
	
	private static FlightLegInstance getNextLeg( Duration maxLayoverTime, Integer totalSeatsMin, LocalDate arrivalDate, LocalTime arrivalTime, String airport  ) {
		
		FlightLegDAOJDBCImpl legDAO 	= new FlightLegDAOJDBCImpl();
		
		LocalDate arrivalPlusOne  = arrivalDate.plus(1, ChronoUnit.DAYS);
		List<FlightLeg> scheduledLegs 	= legDAO.searchLegsByDeparture(arrivalDate, arrivalPlusOne, totalSeatsMin, airport);		
		List<FlightLeg> legsWithinWindow = getLegsWithinLayoverTime(scheduledLegs, arrivalTime, maxLayoverTime);
		
		/**
		 * Right now use Uniform Distribution and pick a leg within this window of time
		 * TODO - use better distribution that takes into account timing of layovers
		 */
		//
		//Weight the distribution based on the capacity of the plane   
		//
		//assign probabilty based on capacity of the plane 
		//
		Random randomizer = new Random();
		if(legsWithinWindow.size() == 0) {
			System.out.println("This passenger is lost!");
			return null;
		} else {
			FlightLeg leg = legsWithinWindow.get( randomizer.nextInt( legsWithinWindow.size() ) );
			LocalDate departureDate = arrivalDate;
			if(leg.getDepartureTimeUTC().isBefore( arrivalTime ))		
				departureDate = departureDate.plus(1, ChronoUnit.DAYS);
		
			return new FlightLegInstance(leg, departureDate);
		}
	}
	
	/**
	 * Pick a random leg from entire leg collection. java.util.Random should be a uniform distribution.
	 * 
	 * @param legInstances
	 * @return
	 */
	private static FlightLegInstance pickStartingLeg(LocalDate startDate, LocalDate endDate, Integer totalSeatsMin, String airportCode ) {
		
		FlightLegDAOJDBCImpl legDAO 	= new FlightLegDAOJDBCImpl();
		List<FlightLeg> scheduledLegs 	= legDAO.searchLegsByDeparture(startDate, endDate, totalSeatsMin, airportCode);		
		
		//Convert scheduled legs into actual leg instances inside the start/end time window
		List<FlightLegInstance> legInstances = getLegsWithinTimePeriod(startDate, endDate, scheduledLegs);

		//Probably should initialize the random generator outside of this block
		return legInstances.get(new Random().nextInt(legInstances.size()));
	}
	
	private static List<FlightLeg> getLegsWithinLayoverTime(List<FlightLeg> legs, LocalTime arrivalTime, Duration maxLayOverTime) {
		
		List<FlightLeg> result = new ArrayList<FlightLeg>();		
		for(FlightLeg leg : legs){
			if( TimeUtil.getInstance().isLayoverCompatible(arrivalTime, leg.getDepartureTimeUTC(), maxLayOverTime.toMinutes()) ) {
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
