package com.eha.grits.test;

import java.time.Duration;
import java.time.LocalTime;

public class TestTimeFunctions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LocalTime 	testArrival 			= LocalTime.of(21, 59);
		Duration	pastMidnightDuration 	= Duration.ofHours(2);
		
		if(testArrival.plus(pastMidnightDuration).isBefore( testArrival ) ) {
			//we triped over the midnight boundary
			System.out.println("Tripped past midnight ");
		} else {
			System.out.println("Didn't trip past midnight");
		}
			
		
	}

}
