package com.eha.grits;

import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.db.FlightLegDAO;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.db.FlightLegDAOMongoImpl;
import com.eha.grits.util.FlightToLegs;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

/**
 * Main entry point
 * 
 * Consume mongo flights collection, parse out legs, figure out arrival and departure times, write legs to legs mongoDB
 * @author brocka
 */
public class ConvertFlightsToLegs {

	public static void main(String[] args) {
		
		String 	mongoHost 	= "localhost";
		int 	mongoPort	= 27017;
		String  mongoDb		= "grits-net-meteor";
		String  flightsCol	= "flights";
		String  legsCollection 	= "legs";
		
		for(String arg : args){
			if(arg.startsWith("--mongohost") ) {
				mongoHost = arg.substring( arg.indexOf("=") + 1 );
			}
			if(arg.startsWith("--mongoport") ) {
				mongoPort = Integer.parseInt(arg.substring( arg.indexOf("=") + 1));
			}
			if(arg.startsWith("--mongodb") ) {
				mongoDb =  arg.substring( arg.indexOf("=") + 1);
			}
			if(arg.startsWith("--flightscol") ) {
				flightsCol = arg.substring( arg.indexOf("=") + 1 );
			}
		}
	 
		Date d = new Date();
		
		updateMongoDB(mongoHost, mongoPort, mongoDb, flightsCol, legsCollection );
		
		System.out.println("Elapsed time: " + (new Date().getTime() - d.getTime()) + " ms");

	}
	
	public static void updateMongoDB(String mongoHost, int mongoPort, String mongoDb, String flightsCol, String legsCollection) {
		
		MongoClient mongoClient = new MongoClient( mongoHost , mongoPort );
		MongoDatabase db = mongoClient.getDatabase( mongoDb );
		FindIterable<Document> iterable = db.getCollection( flightsCol ).find();
		
		db.getCollection( legsCollection ).createIndex( new Document("flightID", 1) );
		db.getCollection( legsCollection ).createIndex( new Document("departureAirport._id", 1) );
		db.getCollection( legsCollection ).createIndex( new Document("arrivalAirport._id", 1) );
		db.getCollection( legsCollection ).createIndex( new Document("effectiveDate", 1) );
		db.getCollection( legsCollection ).createIndex( new Document("discontinuedDate", 1) );
		
		iterable.forEach(new Block<Document>() {
			public void apply(final Document document) {		
				try {
			    	List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
			    	for(FlightLeg leg : legs) {	
			    		FlightLegDAO legDAO = new FlightLegDAOMongoImpl();
			    		legDAO.setHost(mongoHost);
			    		legDAO.setPort(mongoPort);
			    		legDAO.setDB(mongoDb);	
			    		legDAO.create(leg);
			    	}
				}
			    catch(Exception e){
			    	e.printStackTrace();
		    	}				 
		    }
		});
		System.out.println("Done");
		mongoClient.close();
	}
	
	public static void updateSQLDB(String mongoHost, int mongoPort, String mongoDb) {
		
		MongoClient mongoClient = new MongoClient( mongoHost , mongoPort );
		MongoDatabase db = mongoClient.getDatabase( mongoDb );
		FindIterable<Document> iterable = db.getCollection("flights").find();
		
		iterable.forEach(new Block<Document>() {
			public void apply(final Document document) {
		    	List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
		    	for(FlightLeg leg : legs) {
		    		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
					int result = legDAO.create(leg);
					if( result == 0) {
						System.out.println("[" + result +  "]: ERROR Leg Creation:" + leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " DepartureUTC: " + leg.getDepartureTimeUTC() + " ArrivalUTC: " + leg.getArrivalTimeUTC());
						System.exit(0);
					}
		    	}
		    }
		});
		System.out.println("Done");
		mongoClient.close();
	}
}
