package com.eha.grits.flow;

import java.time.LocalDate;

import com.eha.grits.db.FlightLeg;

/**
 * Represents actual Flight Leg, with a real day and time, an actual flight
 * @author brocka
 *
 */

public class FlightLegInstance extends FlightLeg{

	private LocalDate instanceDate;
		
	public FlightLegInstance(FlightLeg leg, LocalDate instanceDate) {
		
		super(leg.getFlightID(), leg.getDepartureAirportCode(), leg.getDepartureAirportLat(), leg.getDepartureAirportLng(), leg.getArrivalAirportCode(), leg.getArrivalAirportLat(), leg.getArrivalAirportLng(), leg.getEffectiveDate(), leg.getDiscontinuedDate(), leg.getDepartureTimeUTC(),
				leg.getArrivalTimeUTC(), leg.isDay1(), leg.isDay2(), leg.isDay3(), leg.isDay4(), leg.isDay5(), leg.isDay6(), leg.isDay7(), leg.getWeeklyFrequency(), leg.getTotalSeats());
		
		this.instanceDate = instanceDate;
	}

	public LocalDate getInstanceDate() {
		return instanceDate;
	}

	public void setInstanceDate(LocalDate instanceDate) {
		this.instanceDate = instanceDate;
	}
	 
	

}
