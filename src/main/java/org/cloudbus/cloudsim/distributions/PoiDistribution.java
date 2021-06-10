package org.cloudbus.cloudsim.distributions;

import javax.swing.text.Position;

// import org.apache.commons.math3.distribution
import org.apache.commons.math3.distribution.PoissonDistribution;

public class PoiDistribution {

    // double meanValue = 5;
    PoissonDistribution pd = null;

    public PoiDistribution(double meanValue) {
        pd = new PoissonDistribution(meanValue);
    }

    public int sample() {
        return pd.sample();
    }

    public static void main(String[] args) {
        double meanValue = 5;
        PoiDistribution poisson = new PoiDistribution(meanValue);
        System.out.println("==============================================");

        int[] result = poisson.pd.sample(100);
        for (int i = 0; i < 10; i++) {
            System.out.println("the" + i + "th number: " + poisson.pd.sample());
        }
    }

}