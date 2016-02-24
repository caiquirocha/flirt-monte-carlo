package com.eha.grits.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.eha.grits.db.FlightLeg;

public class FlightToLegs {

	   private static FlightToLegs instance = null;
	   
	   protected FlightToLegs() {
	   }
	   public static FlightToLegs getInstance() {
	      if(instance == null) {
	         instance = new FlightToLegs();
	      }
	      return instance;
	   }

	@SuppressWarnings("unchecked")
	public List<FlightLeg> getLegsFromFlightRecord(Document doc) {
			
			List<FlightLeg> legs = new ArrayList<FlightLeg>();
			
			String 		flightId				= (String) doc.get( "_id" );
			LocalDate 	effectiveDate 			= convertDateToUTC( doc.getDate("effectiveDate") );
			LocalDate 	discontinuedDate 		= convertDateToUTC( doc.getDate("discontinuedDate") );  
			 
			String 		departureTimePub  		= (String) doc.get( "departureTimePub" );	
			Integer 	departureUTCVariance  	= (Integer) doc.get( "departureUTCVariance" );	
			LocalTime   departureTimeUTC 		= convertTimeToUTC(departureTimePub, departureUTCVariance);
			Document 	departureAirport		= (Document) doc.get("departureAirport");
			String 		departureAirportCode	= (String) departureAirport.get("_id");
			Document	departureAirportLoc		= (Document)departureAirport.get("loc");
			
			ArrayList<Double> departureCoord 	=  (ArrayList<Double>) departureAirportLoc.get( "coordinates" );
			Double 		departureLng 			= departureCoord.get(0);
			Double 		departureLat 			= departureCoord.get(1);
			
			String 		arrivalTimePub  		= (String) doc.get( "arrivalTimePub" );	
			Integer 	arrivalUTCVariance  	= (Integer) doc.get( "arrivalUTCVariance" );	
			LocalTime   arrivalTimeUTC 			= convertTimeToUTC(arrivalTimePub, arrivalUTCVariance);
			Document 	arrivalAirport			= (Document) doc.get("arrivalAirport");
			String	 	arrivalAirportCode		= (String) arrivalAirport.get("_id");
			
			Document	arrivalAirportLoc		= (Document) arrivalAirport.get("loc");
			
			ArrayList<Double> arrivalCoord 		=  (ArrayList<Double>) arrivalAirportLoc.get( "coordinates" );
			Double 		arrivalLng 				= arrivalCoord.get(0);
			Double 		arrivalLat 				= arrivalCoord.get(1);
			
			Duration 	totalFlightDuration 	= Duration.between(departureTimeUTC, arrivalTimeUTC);
			
			//If arrivalTimeUTC < departureTimeUTC then we have crossed over midnight.
			if(arrivalTimeUTC.isBefore(departureTimeUTC)) {
				totalFlightDuration		= Duration.between(departureTimeUTC, LocalTime.MAX).plus(Duration.between(LocalTime.MIDNIGHT, arrivalTimeUTC));
			} 
			
			Boolean day1	= doc.getBoolean("day1");
			Boolean day2	= doc.getBoolean("day2");
			Boolean day3	= doc.getBoolean("day3");
			Boolean day4	= doc.getBoolean("day4");
			Boolean day5	= doc.getBoolean("day5");
			Boolean day6	= doc.getBoolean("day6");
			Boolean day7	= doc.getBoolean("day7");
			
			Integer	weeklyFreq = doc.getInteger("weeklyFrequency") != null ? doc.getInteger("weeklyFrequency") : 0;
			Integer	totalSeats = doc.getInteger("totalSeats") != null ? doc.getInteger("totalSeats") : 0;
			
			if( doc.getInteger( "stops" ) > 0) {
				
				//Assume 1.5 hour average ground time on multi-leg flight
				//Have Toph do some research
				Duration 	groundTime					= Duration.ofMinutes(90);
				
				ArrayList<Document> stops 	= (ArrayList<Document>) doc.get( "stopCodes" );			
				
				Double legDepartureLng = departureLng;
				Double legDepartureLat = departureLat;
				
				Double totalFlightDistance = new Double(0);
				//Need to do two passes, first pass through to calculate rough estimate of distance flown A->B->C->D
				for (Document stop : stops) {	
					Document loc 				=  (Document) stop.get( "loc" );
					ArrayList<Double> legArrivalCoord =  (ArrayList<Double>) loc.get( "coordinates" );
					Double 		legArrivalLng 	= legArrivalCoord.get(0);
					Double 		legArrivalLat 	= legArrivalCoord.get(1);
					Double legDistance 			= greatCircleDistance(legDepartureLat, legDepartureLng, legArrivalLat, legArrivalLng);			
					
					totalFlightDistance 		+= legDistance;				
					legDepartureLng 			= legArrivalLng;
					legDepartureLat 			= legArrivalLat;
				}
				//Add the final leg to total distance, the destination on the main flight record (non-layover stop code)
				totalFlightDistance +=  greatCircleDistance(legDepartureLat, legDepartureLng, arrivalLat, arrivalLng);
				
				//Re-init First leg departure as the same as the whole flight departure location and time
				LocalTime	legMidpointTimeUTC = departureTimeUTC;
				legDepartureLng = departureLng;
				legDepartureLat = departureLat;
				String legDepartureCode = departureAirportCode;
				LocalTime legArrivalTime = null;
				
				//Now use the total distance information to our advantage
				for (Document stop : stops) {		
					Document loc 						=  (Document) stop.get( "loc" );
					ArrayList<Double> legArrivalCoord 	=  (ArrayList<Double>) loc.get( "coordinates" );

					String 		legArrivalCode	= (String) stop.get("_id");
					Double 		legArrivalLng 	= legArrivalCoord.get(0);
					Double 		legArrivalLat 	= legArrivalCoord.get(1);
					
					Double legDistance 			= greatCircleDistance(legDepartureLat, legDepartureLng, legArrivalLat, legArrivalLng);				
					
					//With percentage of flight distance wise and total flight duration, estimate leg duration assuming equally spaced out legs on Multi-Leg Flight.					
					Double percentageOFlight	= legDistance / totalFlightDistance;
					Duration legDuration 		= getLegDuration(legMidpointTimeUTC, totalFlightDuration, percentageOFlight);
					legMidpointTimeUTC			= legMidpointTimeUTC.plus(legDuration.toNanos(), ChronoUnit.NANOS);
						
					LocalTime legDepartureTime  = legMidpointTimeUTC.plus(groundTime.dividedBy(2));
					legArrivalTime  			= legMidpointTimeUTC.minus(groundTime.dividedBy(2));
										
					if(flightId == null || legDepartureCode == null || legArrivalCode == null || effectiveDate == null || discontinuedDate == null || legDepartureTime == null || legArrivalTime == null || day1 == null || day2 == null || day3 == null || day4 == null || day5 == null || day6 == null || day7 == null || weeklyFreq == null || totalSeats == null) {
						System.out.println("ERROR TRACE: FlightUtil NULL Pointer: " + flightId);
					} else {
						FlightLeg leg = new FlightLeg(flightId, legDepartureCode, legDepartureLat, legDepartureLng, legArrivalCode, legArrivalLat, legArrivalLng, effectiveDate, discontinuedDate, legDepartureTime, legArrivalTime, day1, day2, day3, day4, day5, day6, day7, weeklyFreq, totalSeats);
						legs.add(leg);
					}
					
					//Next layover will have departure that is equal to this layover arrival
					legDepartureLng 			= legArrivalLng;
					legDepartureLat 			= legArrivalLat;
					legDepartureCode 			= legArrivalCode;
				}
				
				//Final leg of Multi-Leg flight arrives at arrival on main flight document record, but leaves on the last stopcode
				LocalTime lastLegDepartureTime = legArrivalTime.plus(groundTime.dividedBy(2));	 
				
				if(flightId == null || legDepartureCode == null || arrivalAirportCode == null || effectiveDate == null || discontinuedDate == null || lastLegDepartureTime == null || legArrivalTime == null || day1 == null || day2 == null || day3 == null || day4 == null || day5 == null || day6 == null || day7 == null || weeklyFreq == null || totalSeats == null) {
					System.out.println("ERROR TRACE: FlightUtil NULL Pointer: " + flightId);
				} else {
					FlightLeg leg = new FlightLeg(flightId, legDepartureCode, legDepartureLat, legDepartureLng, arrivalAirportCode, arrivalLat, arrivalLng, effectiveDate, discontinuedDate, lastLegDepartureTime, legArrivalTime, day1, day2, day3, day4, day5, day6, day7, weeklyFreq, totalSeats);
					legs.add(leg);
				}						
			} else {
				if(flightId == null || departureAirportCode == null || arrivalAirportCode == null || effectiveDate == null || discontinuedDate == null || departureTimeUTC == null || arrivalTimeUTC == null || day1 == null || day2 == null || day3 == null || day4 == null || day5 == null || day6 == null || day7 == null || weeklyFreq == null || totalSeats == null) {
					System.out.println("ERROR TRACE: FlightUtil NULL Pointer: " + flightId);
				} else {
					FlightLeg leg = new FlightLeg(flightId, departureAirportCode, departureLat, departureLng, arrivalAirportCode, arrivalLat, arrivalLng, effectiveDate, discontinuedDate, departureTimeUTC, arrivalTimeUTC, day1, day2, day3, day4, day5, day6, day7, weeklyFreq, totalSeats);
					legs.add(leg);
				}
			}
			
			return legs;
		}
	   
		private Duration getLegDuration(LocalTime departureTime, Duration flightDuration, Double legPercentage) {
			long durationLong	= flightDuration.toNanos();
			long durationLeg 	= (long) ((double)durationLong * legPercentage);
			return Duration.ofNanos(durationLeg);
		}
		
		//Returns in Miles
		private double greatCircleDistance(double lat1, double lon1, double lat2, double lon2) {
			double theta = lon1 - lon2;
			double distance = Math.sin(deg2rad(lat1)) 
					* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
					* Math.cos(deg2rad(lat2)) 
					* Math.cos(deg2rad(theta));
			distance = Math.acos(distance);
			distance = rad2deg(distance);
			distance = distance * 60 * 1.1515;
			return distance;
		}
		private double deg2rad(double deg) {
			return (deg * Math.PI / 180.0);
		}
		private double rad2deg(double rad) {
			return (rad * 180 / Math.PI);
		}	 
		private LocalDate convertDateToUTC (Date date) {
			//When you parse an ISO date using mongo driver it translates the date into the system timezone. We really want a UTC localdate instead!
			return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
		}
		private LocalTime convertTimeToUTC (String publishedTime, Integer utcVariance) {
			LocalTime 	timeUTC 	 	= LocalTime.parse(publishedTime);
			int hours 	= Math.abs(utcVariance / 100);
			int minutes = Math.abs(utcVariance % 100);

			if ( utcVariance > 0 ) {
				timeUTC = timeUTC.minus(hours, ChronoUnit.HOURS).minus(minutes, ChronoUnit.MINUTES);
			} else {
				timeUTC = timeUTC.plus(hours, ChronoUnit.HOURS).plus(minutes, ChronoUnit.MINUTES);
			}
			return timeUTC;	
		}
}
