package com.eha.grits.tools;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.eha.grits.db.FlightLeg;
import com.eha.grits.util.FlightToLegs;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class TestDuration {

	public static void main(String[] args) {
	
	
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase db = mongoClient.getDatabase("grits");
			FindIterable<Document> iterable = db.getCollection("flights").find();
			System.out.println("Start");
			iterable.forEach(new Block<Document>() {
				public void apply(final Document document) {		
					
					List<Period> periods = new ArrayList<Period>();
					List<FlightLeg> legs = FlightToLegs.getInstance().getLegsFromFlightRecord(document);
			    	for(FlightLeg leg : legs) {
			    		LocalDate eff = leg.getEffectiveDate();
			    		LocalDate dis = leg.getDiscontinuedDate();
			    		Period period = Period.between(eff, dis);
			    		periods.add(period);
			    	}		
										
			    	long sum = 0;
		    	    for (Period p : periods) {
		    	        sum += p.getDays();
		    	    }
		    	    long average = (long) ((double)sum / periods.size());
		    	  	System.out.println(average +" days long");		    	  
				}
			});
			System.out.println("Done");
			mongoClient.close();
		
		
	}

}
