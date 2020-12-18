package org.cloudbus.cloudsim.sdn.example.topogenerators;

// import org.apache.commons.math3.distribution.paretoDistribution;

import org.apache.commons.math3.distribution.ParetoDistribution;

public class PartoTest {
    public static void main(String[] args) {
        double scale = 10;
        double shape = 1.111111111;
        ParetoDistribution pd = new ParetoDistribution(scale, shape);

        double cp11 = pd.cumulativeProbability(10, 50);
        System.out.println(cp11);

        double cp12 = pd.cumulativeProbability(50);
        System.out.println(cp12);

        double cp21 = pd.cumulativeProbability(50, 100);
        System.out.println(cp21);

        double cp31 = pd.cumulativeProbability(10, 100);
        System.out.println(cp31);

        System.out.println("=====================================");

        double cp32 = pd.cumulativeProbability(100);
        System.out.println(cp32);

        double cp41 = pd.cumulativeProbability(100, 150);
        System.out.println(cp41);

        double cp42 = pd.cumulativeProbability(150);
        System.out.println(cp42);

        double mean_value = pd.getNumericalMean();
        double var_value = pd.getNumericalVariance();
    }

}