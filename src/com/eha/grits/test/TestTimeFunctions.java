package com.eha.grits.test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.eha.grits.tools.TimeUtil;

public class TestTimeFunctions {

	public static void main(String[] args) {

		LocalTime depart	= LocalTime.of(4, 30);
		int maxLayoverMinutes = 8 * 60;
		
		System.out.println("Test 8 hour layover past midnight");		
		for(int i=0; i<24; i++){
			LocalTime arriv = LocalTime.of(i, 0);
			
			boolean bOK = TimeUtil.getInstance().isLayoverCompatible(arriv, depart, maxLayoverMinutes );			
			System.out.println("Arrive: " + arriv.toString() + " Depart: " + depart.toString() + " " + bOK);
		}	
	}
	
}
