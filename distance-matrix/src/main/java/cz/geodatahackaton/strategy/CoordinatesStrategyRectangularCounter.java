package cz.geodatahackaton.strategy;

import java.math.BigDecimal;

/**
 * Strategy counter.
 *
 * @author cubeek
 */
public class CoordinatesStrategyRectangularCounter {

    private final int size;

    private final int maxRequestSize;

    private int sideSize;

    private int numberOfRequests;

    private int total;

    public CoordinatesStrategyRectangularCounter(final int size, final int maxRequestSize) {
        this.size = size;
        this.maxRequestSize = maxRequestSize;

        evaluate();
    }

    private void evaluate() {
        int a = evalA(size);
        int numberOfRequests = 0;
        int total = 0;
        int i = 0;

        while (true) {
            int result = (size - (a * i++));
            if (result >= 0) {
                total += result;
                numberOfRequests += 1;
            } else {
                break;
            }
        }

        this.sideSize = a;
        this.numberOfRequests = numberOfRequests;
        this.total = total;
    }

    private int evalA(int n) {
        int maxReqA = BigDecimal.valueOf(Math.sqrt(maxRequestSize)).intValue();
        int aN = BigDecimal.valueOf(Math.sqrt(n)).intValue();
        int aN1 = aN + 1;
        int baseA = (Math.abs(n - (aN ^ 2)) < Math.abs(n - (aN1 ^ 2))) ? aN : aN1;
        if (baseA > maxReqA) {
            System.err.println("This many requests can't yet be processed!");
            return -1;
        }
        return baseA;
    }

    public int getTotalCoordinates() {
        return total;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public int getSideSize() {
        return sideSize;
    }

}
