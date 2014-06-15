package cz.geodatahackaton;

import cz.geodatahackaton.strategy.CoordsStrategyRectangularCounter;

import java.math.BigDecimal;

/**
 * @author cubeek
 */
public class DistanceMatrixMeter {

    public static final int DATA_SIZE = 500;

    public static final int DATA_LIMIT = 100;

    public static void main(String[] args) {
        final CoordsStrategyRectangularCounter counter = new CoordsStrategyRectangularCounter(DATA_SIZE, DATA_LIMIT);

        System.out.println("=== Input ===");
        System.out.println(String.format("Value of n=%d", counter.getSize()));
        System.out.println(String.format("Value of a=%d", counter.getSideSize()));

        System.out.println("=== Per direction ===");
        System.out.println(String.format("Expected amount of requests: %d", counter.getNumberOfRequestsSignleDirection()));
        System.out.println(String.format("Expected amount of elements: %d", counter.getTotalSignleDirection()));

        System.out.println("=== Total ===");
        System.out.println(String.format("Expected amount of requests: %d", counter.getNumberOfRequests()));
        System.out.println(String.format("Expected amount of elements: %d", counter.getTotal()));

        System.out.println("=== GApps Limits ===");
        System.out.println(String.format("Minimum time waiting for requests (s): %d", counter.getMinTime()));
    }

}
