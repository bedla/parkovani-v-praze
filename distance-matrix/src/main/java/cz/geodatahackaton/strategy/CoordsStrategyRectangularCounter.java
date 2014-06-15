package cz.geodatahackaton.strategy;

import java.math.BigDecimal;

/**
 * Strategy counter.
 *
 * @author cubeek
 */
public class CoordsStrategyRectangularCounter {

    private final int size;

    private final int requestDataLimit;

    private int sideSize;

    private int numberOfRequests;

    private int total;

    public CoordsStrategyRectangularCounter(final int size, final int requestDataLimit) {
        this.size = size;
        this.requestDataLimit = requestDataLimit;
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
                int req = result /requestDataLimit ;
                if (result % requestDataLimit > 0) {
                    req += 1;
                }
                numberOfRequests += req;
            } else {
                break;
            }
        }

        this.sideSize = a;
        this.numberOfRequests = numberOfRequests;
        this.total = total;
    }

    private int evalA(int n) {
        int aN = BigDecimal.valueOf(Math.sqrt(n)).intValue();
        int aN1 = aN + 1;
        return (Math.abs(n - (aN * aN)) < Math.abs(n - (aN1 * aN1))) ? aN : aN1;
    }

    public int getTotalSignleDirection() {
        return total;
    }

    public int getTotal() {
        return total * 4;
    }

    public int getNumberOfRequestsSignleDirection() {
        return numberOfRequests;
    }

    public int getNumberOfRequests() {
        return numberOfRequests * 4;
    }

    public int getMinTime() {
        return numberOfRequests * 40;
    }

    public int getSideSize() {
        return sideSize;
    }

    public int getRequestDataLimit() {
        return requestDataLimit;
    }

    public int getSize() {
        return size;
    }
}
