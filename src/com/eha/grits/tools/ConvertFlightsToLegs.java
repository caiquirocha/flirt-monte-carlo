package com.eha.grits.tools;

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

/**
 * Consume mongo flights collection, parse out legs, figure out arrival and departure times, write legs to legs database
 * @author brocka
 */
public class ConvertFlightsToLegs {

	public static void main(String[] args) {

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase db = mongoClient.getDatabase("grits");
		
		FindIterable<Document> iterable = db.getCollection("flights").find();

		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
		    	for(FlightLeg leg : legs) {
		    		FlightLegDAO legDAO = new FlightLegDAOJDBCImpl();
					int result = legDAO.create(leg);
					if( result == 0) {
						System.out.println("[" + result +  "]: ERROR Leg Creation:" + leg.getDepartureAirportCode() + "->" + leg.getArrivalAirportCode() + " DepartureUTC: " + leg.getDepartureTimeUTC() + " ArrivalUTC: " + leg.getArrivalTimeUTC());
					}
		    	}
		    }
		});
		System.out.println("Done");
		mongoClient.close();

	}

}
