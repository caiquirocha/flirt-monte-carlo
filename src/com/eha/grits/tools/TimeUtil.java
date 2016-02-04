package com.eha.grits.tools;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.eha.grits.db.FlightLeg;

public class TimeUtil {

	   private static TimeUtil instance = null;
	   
	   protected TimeUtil() {
	   }
	   public static TimeUtil getInstance() {
	      if(instance == null) {
	         instance = new TimeUtil();
	      }
	      return instance;
	   }
	   
	   /**
	    * Given two LocalTimes, are they within a compatible window of time for layover selection given a max layover in minutes?
	    * @param arrivalTime
	    * @param departureTime
	    * @param maxLayOverMin
	    * @return
	    */
	   public boolean isLayoverCompatible(LocalTime arrivalTime, LocalTime departureTime, long maxLayOverMin) {
			
			boolean bResult = false;
			if(arrivalTime.plus( Duration.ofMinutes( maxLayOverMin) ).isBefore( arrivalTime ) ) {
				//Tripped the midnight boundary
				LocalTime offset = arrivalTime.plus( Duration.ofMinutes( maxLayOverMin) );
				if( (departureTime.isAfter(arrivalTime) && departureTime.isBefore(LocalTime.MAX))  ||
						departureTime.isAfter(LocalTime.MIDNIGHT) && departureTime.isBefore( offset )) {
					return true;
				}
			}
			else {			
				long diff = arrivalTime.until( departureTime, ChronoUnit.MINUTES );
				if( diff <= maxLayOverMin && diff > 0 ) {
					return true;
				}    
			}
			return bResult;
		}
	   
		/**
		 * See if this leg flies on this day of the week
		 * @param leg
		 * @param date
		 * @return
		 */
		public  boolean fliesThisDay(FlightLeg leg, LocalDate date) {
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
