package me.logwet.marathon.util;

import org.apache.commons.math3.distribution.BinomialDistribution;

public class VariableBinomialDistribution {
    private final int numberOfTrials;

    public VariableBinomialDistribution(int trials) {
        numberOfTrials = trials;
    }

    public double getNumericalMean(double[] probabilities) {
        double mean = 0D;

        for (int i = 0; i <= this.numberOfTrials; i++) {
            mean += i * this.probability(i, probabilities[i]);
        }

        return mean;
    }

    public double probability(int x, double p) {
        return (new BinomialDistribution(this.numberOfTrials, p)).probability(x);
    }
}
