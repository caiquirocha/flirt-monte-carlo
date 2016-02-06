package com.eha.grits.db;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class FlightLegDAOMongoImpl implements FlightLegDAO {
	
	private String 	host		= "localhost";
	private int 	port		= 27017;
	
	private String database 	= "grits";
	private String collection 	= "legs";
	
	@Override
	public int create(FlightLeg leg)   {
		
		MongoClient mongoClient = MongoConnectionFactory.getInstance(this.host, this.port).getConnection();		
		
		MongoDatabase db = mongoClient.getDatabase( database );
		MongoCollection<Document> legsCollection = db.getCollection( collection );
		
		Document doc = new Document();
		 
		doc.append("flightID", leg.getFlightID() );
			
			Document docDepartureAirport = new Document();
				docDepartureAirport.append("_id",  leg.getDepartureAirportCode() );
					Document docLocationDeparture = new Document();
					List<Double> coord = new ArrayList<Double>();
					coord.add(leg.getDepartureAirportLng());
					coord.add(leg.getDepartureAirportLat());
					docLocationDeparture.append("coordinates", coord);
				docDepartureAirport.append("loc", docLocationDeparture );	
			doc.append("departureAirport", docDepartureAirport);
			
			Document docArrivalAirport = new Document();
				docArrivalAirport.append("_id",  leg.getArrivalAirportCode() );
					Document docLocationArrival = new Document();
					List<Double> coordArrival = new ArrayList<Double>();
					coordArrival.add(leg.getArrivalAirportLng());
					coordArrival.add(leg.getArrivalAirportLat());
					docLocationArrival.append("coordinates", coordArrival);
				docArrivalAirport.append("loc", docLocationArrival );	
			doc.append("arrivalAirport", docArrivalAirport);
			
			Date effectiveDate = Date.from( leg.getEffectiveDate().atStartOfDay(ZoneId.of("UTC") ).toInstant() );
			doc.append("effectiveDate", effectiveDate );
			
			Date discontinuedDate = Date.from( leg.getDiscontinuedDate().atStartOfDay(ZoneId.of("UTC") ).toInstant() );
			doc.append("discontinuedDate", discontinuedDate );
			
			doc.append("departureTimeUTC", leg.getDepartureTimeUTC().toString() );
			doc.append("arrivalTimeUTC", leg.getArrivalTimeUTC().toString() );
			doc.append("day1", leg.isDay1() );
			doc.append("day2", leg.isDay2() );
			doc.append("day3", leg.isDay3() );
			doc.append("day4", leg.isDay4() );
			doc.append("day5", leg.isDay5() );
			doc.append("day6", leg.isDay6() );
			doc.append("day7", leg.isDay7() );
			doc.append("weeklyFrequency", leg.getWeeklyFrequency() );
			doc.append("totalSeats", leg.getTotalSeats() );
			
			System.out.println(doc);
			legsCollection.insertOne(doc);
			
			return 1;
	}
	
	@Override
	public void delete(long id) {
		//Not used
	}
	
	@Override
	public void update(long id, FlightLeg leg) {
		//Not used
	}
	
	@Override
	public List<FlightLeg> searchLegsByDeparture(LocalDate effectiveDate, LocalDate discontinuedDate, int totalSeats,
			String airportDepartureCode) {
		//This is not going to be used.
		return null;
	}

	@Override
	public void setHost(String host) {
		this.host = host;		
	}

	@Override
	public void setPort(int port) {
		this.port = port;		
	}

}
