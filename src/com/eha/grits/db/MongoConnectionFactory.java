package com.eha.grits.db;

import com.mongodb.MongoClient;

public class MongoConnectionFactory {

	   private static MongoConnectionFactory instance = null;
	   
	   
	   private static MongoClient 	_mongoClient 	= null;   
	   private static final String 	_host 			= "localhost";
	   private static final int 	_port 			= 27017;
	
		
	   protected MongoConnectionFactory() { }
	   
	   public static MongoConnectionFactory getInstance() {
	      if(instance == null) {
	         instance = new MongoConnectionFactory();
	         
	         _mongoClient = new MongoClient( _host , _port );
	      }
	      return instance;
	   }
	   
	   public MongoClient getConnection() {
		   return _mongoClient;
	   }
}
