package com.eha.grits.test;

import java.util.HashMap;

import com.eha.grits.util.DistributedRandomNumberGenerator;

public class TestRandomNumbers {

	public static void main(String[] args) {

        DistributedRandomNumberGenerator numberLegsProbabilty = new DistributedRandomNumberGenerator();
        numberLegsProbabilty.addNumber(1, 0.6772732d);
        numberLegsProbabilty.addNumber(2, 0.2997706d);
        numberLegsProbabilty.addNumber(3, 0.0211374d);
        numberLegsProbabilty.addNumber(4, 0.0016254d);
        numberLegsProbabilty.addNumber(5, 0.0001632d);
        numberLegsProbabilty.addNumber(6, 0.0000215d);
        numberLegsProbabilty.addNumber(7, 0.0000072d);
        numberLegsProbabilty.addNumber(8, 0.0000012d);
        numberLegsProbabilty.addNumber(9, 0.0000002d);
        numberLegsProbabilty.addNumber(10, 0.0000001d);
        
        int testCount = 1000000;
        HashMap<Integer, Double> test = new HashMap<>();
        for (int i = 0; i < testCount; i++) {
            int random = numberLegsProbabilty.getDistributedRandomNumber();
            System.out.println(random);
            test.put(random, (test.get(random) == null) ? (1d / testCount) : test.get(random) + 1d / testCount);
        }
        System.out.println(test.toString());
	}

}
