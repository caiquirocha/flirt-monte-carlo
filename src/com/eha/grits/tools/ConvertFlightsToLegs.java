package com.eha.grits.tools;

import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
 * Consume mongo flights collection, parse out legs, figure out arrival and departure times, write legs to legs database
 * @author brocka
 */
public class ConvertFlightsToLegs {

	public static void main(String[] args) {
		
		String 	mongoHost 	= "localhost";
		int 	mongoPort	= 27017;
		String  mongoDb		= "grits";
		String  flightsCol	= "flights";
		 
		for(String arg : args){
			if(arg.startsWith("--mongohost") ) {
				mongoHost = arg.substring( arg.indexOf("=") + 1 );
			}
			if(arg.startsWith("--mongoport") ) {
				mongoPort = Integer.parseInt(arg.substring( arg.indexOf("=") + 1));
			}
			if(arg.startsWith("--flightscol") ) {
				flightsCol = arg.substring( arg.indexOf("=") + 1 );
			}
		}
	 
		Date d = new Date();
		updateMongoDB(mongoHost, mongoPort, mongoDb, flightsCol );
		System.out.println("Elapsed time: " + (new Date().getTime() - d.getTime()) + " ms");

	}
	
	public static void updateMongoDB(String mongoHost, int mongoPort, String mongoDb, String flightsCol) {
		
		MongoClient mongoClient = new MongoClient( mongoHost , mongoPort );
		MongoDatabase db = mongoClient.getDatabase( mongoDb );
		FindIterable<Document> iterable = db.getCollection( flightsCol ).find();
		
		System.out.println("Start");
		iterable.forEach(new Block<Document>() {
			public void apply(final Document document) {		
		    	List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
		    	for(FlightLeg leg : legs) {	
		    		FlightLegDAO legDAO = new FlightLegDAOMongoImpl();
		    		legDAO.create(leg);
		    	}				 
		    }
		});
		System.out.println("Done");
		mongoClient.close();
	}
	
	public static void updateSQLDB() {
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase db = mongoClient.getDatabase("grits");
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
