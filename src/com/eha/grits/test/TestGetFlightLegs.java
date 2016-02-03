package com.eha.grits.test;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.db.FlightLegDAO;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.util.FlightToLegs;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class TestGetFlightLegs {

	/**
	 * Testing ability to parse the 'flights' collection we currently have in the mongodb
	 * and create a brand new relational database with all flight leg information
	 * 
	 * This will break up flights that are MULTI-LEG and does best guess estimates on departure
	 * and arrival times for multi-leg flights
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//testGetLegsFromFlightRecord()
		//testSearchLegsTable
		//testGetLegsFromFlightRecord();
	}
	
	
	/**
	 * Test searching records by 
	 */
	public static void testSearchLegsTable() {
		LocalDate effectiveDate = LocalDate.of(2016, 2, 17);
		LocalDate discontinuedDate = LocalDate.of(2016, 2, 27);
		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
		List<FlightLeg> legs = legDAO.searchLegsByDeparture(effectiveDate, discontinuedDate, 100, "PBG");
		System.out.println(legs.size());
		for(FlightLeg leg : legs){
			System.out.println(leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " " + leg.getEffectiveDate() + " to " + leg.getDiscontinuedDate());
		}
	}
	
	/**
	 * Connect to mongo database, inspect flights collection, search by airport code, 
	 * test turning document from flights collection into corresponding 'legs' 
	 */
	public static void testGetLegsFromFlightRecord() {
		String airportCode = "JFK";

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase db = mongoClient.getDatabase("grits");
		
		FindIterable<Document> iterable = db.getCollection("flights").find(
			com.mongodb.client.model.Filters.eq("departureAirport._id", airportCode) 
		);

		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
		    	for(FlightLeg leg : legs) {
		    		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
					int result = legDAO.create(leg);
		    		
					System.out.println("[" + result +  "]: Leg Created:" + leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " DepartureUTC: " + leg.getDepartureTimeUTC() + " ArrivalUTC: " + leg.getArrivalTimeUTC());
		    	}
		    }
		});
		mongoClient.close();
	}

}
