package com.eha.grits.test;

import com.eha.grits.db.FlightLegDAO;
import com.eha.grits.db.FlightLegDAOJDBCImpl;
import com.eha.grits.db.FlightLegDAOMongoImpl;

public class TestCreateMongoLegs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlightLegDAO legDAO = new FlightLegDAOMongoImpl();
		legDAO.create(null);
	}

}
