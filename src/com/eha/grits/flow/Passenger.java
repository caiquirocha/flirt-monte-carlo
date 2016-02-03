package com.eha.grits.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulated passenger
 * @author brocka
 *
 */
public class Passenger {

	private List<FlightLegInstance> legs;
	
	public Passenger() {
		legs = new ArrayList<FlightLegInstance>();
	}
	
	public void addLeg(FlightLegInstance leg) {
		this.legs.add(leg);
	}
	public List<FlightLegInstance> getLegs() {
		return this.legs;
	}
}
