package com.eha.grits.tools;

import com.eha.grits.util.DBUtil;

/**
 * Delete leg table, rebuild it, doesn't insert data, just creates the table
 * @author brocka
 *
 */
public class RebuildLegTable {

	public static void main(String[] args) {		
		try {
			DBUtil.dropTable();		
			DBUtil.createFlightLegTable();					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
