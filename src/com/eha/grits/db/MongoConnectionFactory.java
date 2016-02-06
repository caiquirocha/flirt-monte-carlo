package com.eha.grits.db;

import com.mongodb.MongoClient;

public class MongoConnectionFactory {

	   private static MongoConnectionFactory instance = null;	   
	   private static MongoClient 	_mongoClient 	= null;  
	   
	   protected MongoConnectionFactory() { }
	   
	   public static MongoConnectionFactory getInstance(String host, int port) {
	      if(instance == null) {
	         instance = new MongoConnectionFactory();
	         
	         _mongoClient = new MongoClient( host , port );
	      }
	      return instance;
	   }
	   
	   public MongoClient getConnection() {
		   return _mongoClient;
	   }
}
