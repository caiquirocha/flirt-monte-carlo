package com.eha.grits.db;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This represents a scheduled FlightLeg, taken from published flight data. 
 * It does not represent a specific flight in time however.
 * 
 * @author brocka
 *
 */
public class FlightLeg {
	
	private int		id;
	private String 	flightID;
	private String 	departureAirportCode;
	private Double	departureAirportLat;
	private Double	departureAirportLng;
	
	private String 	arrivalAirportCode;
	private Double	arrivalAirportLat;
	private Double	arrivalAirportLng;

	private LocalDate 	effectiveDate;
	private LocalDate 	discontinuedDate;
	private LocalTime 	departureTimeUTC;
	private LocalTime 	arrivalTimeUTC;

	private boolean day1;
	private boolean day2;
	private boolean day3;
	private boolean day4;
	private boolean day5;
	private boolean day6;
	private boolean day7;
	
	private int weeklyFrequency;
	private int totalSeats;
		
	public FlightLeg(String flightID, String departureAirportCode, Double departureAirportLat, Double departureAirportLng, String arrivalAirportCode,
			Double arrivalAirportLat, Double arrivalAirportLng, LocalDate effectiveDate, LocalDate discontinuedDate, LocalTime departureTimeUTC, LocalTime arrivalTimeUTC,
			boolean day1, boolean day2, boolean day3, boolean day4, boolean day5, boolean day6, boolean day7, int weeklyFrequency,
			int totalSeats) {
	
			super();
			
			this.flightID = flightID;
			this.departureAirportCode = departureAirportCode;
			this.departureAirportLat = departureAirportLat;
			this.departureAirportLng = departureAirportLng;
			this.arrivalAirportCode = arrivalAirportCode;
			this.arrivalAirportLat = arrivalAirportLat;
			this.arrivalAirportLng = arrivalAirportLng;
			this.effectiveDate = effectiveDate;
			this.discontinuedDate = discontinuedDate;
			this.departureTimeUTC = departureTimeUTC;
			this.arrivalTimeUTC = arrivalTimeUTC;
			this.day1 = day1;
			this.day2 = day2;
			this.day3 = day3;
			this.day4 = day4;
			this.day5 = day5;
			this.day6 = day6;
			this.day7 = day7;
			
			this.weeklyFrequency = weeklyFrequency;
			this.totalSeats = totalSeats;
 
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlightID() {
		return flightID;
	}
	public void setFlightID(String flightID) {
		this.flightID = flightID;
	}
	public String getDepartureAirportCode() {
		return departureAirportCode;
	}
	public void setDepartureAirportCode(String departureAirportCode) {
		this.departureAirportCode = departureAirportCode;
	}
	public String getArrivalAirportCode() {
		return arrivalAirportCode;
	}
	public void setArrivalAirportCode(String arrivalAirportCode) {
		this.arrivalAirportCode = arrivalAirportCode;
	}
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public LocalDate getDiscontinuedDate() {
		return discontinuedDate;
	}
	public void setDiscontinuedDate(LocalDate discontinuedDate) {
		this.discontinuedDate = discontinuedDate;
	}
	public LocalTime getDepartureTimeUTC() {
		return departureTimeUTC;
	}
	public void setDepartureTimeUTC(LocalTime departureTimeUTC) {
		this.departureTimeUTC = departureTimeUTC;
	}
	public LocalTime getArrivalTimeUTC() {
		return arrivalTimeUTC;
	}
	public void setArrivalTimeUTC(LocalTime arrivalTimeUTC) {
		this.arrivalTimeUTC = arrivalTimeUTC;
	}
	public boolean isDay1() {
		return day1;
	}
	public void setDay1(boolean day1) {
		this.day1 = day1;
	}
	public boolean isDay2() {
		return day2;
	}
	public void setDay2(boolean day2) {
		this.day2 = day2;
	}
	public boolean isDay3() {
		return day3;
	}
	public void setDay3(boolean day3) {
		this.day3 = day3;
	}
	public boolean isDay4() {
		return day4;
	}
	public void setDay4(boolean day4) {
		this.day4 = day4;
	}
	public boolean isDay5() {
		return day5;
	}
	public void setDay5(boolean day5) {
		this.day5 = day5;
	}
	public boolean isDay6() {
		return day6;
	}
	public void setDay6(boolean day6) {
		this.day6 = day6;
	}
	public boolean isDay7() {
		return day7;
	}
	public void setDay7(boolean day7) {
		this.day7 = day7;
	}
	public int getWeeklyFrequency() {
		return weeklyFrequency;
	}
	public void setWeeklyFrequency(int weeklyFrequency) {
		this.weeklyFrequency = weeklyFrequency;
	}
	public int getTotalSeats() {
		return totalSeats;
	}
	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}
	public Double getDepartureAirportLat() {
		return departureAirportLat;
	}
	public void setDepartureAirportLat(Double departureAirportLat) {
		this.departureAirportLat = departureAirportLat;
	}
	public Double getDepartureAirportLng() {
		return departureAirportLng;
	}
	public void setDepartureAirportLng(Double departureAirportLng) {
		this.departureAirportLng = departureAirportLng;
	}
	public Double getArrivalAirportLat() {
		return arrivalAirportLat;
	}
	public void setArrivalAirportLat(Double arrivalAirportLat) {
		this.arrivalAirportLat = arrivalAirportLat;
	}
	public Double getArrivalAirportLng() {
		return arrivalAirportLng;
	}
	public void setArrivalAirportLng(Double arrivalAirportLng) {
		this.arrivalAirportLng = arrivalAirportLng;
	}
	
}
 