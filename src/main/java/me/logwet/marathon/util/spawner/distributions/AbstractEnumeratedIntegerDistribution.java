package me.logwet.marathon.util.spawner.distributions;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.stream.IntStream;

public abstract class AbstractEnumeratedIntegerDistribution extends EnumeratedIntegerDistribution {
    private final int numberOfTrials;

    private final double[] probabilities;
    private final double[] cumulativeProbabilities;

    private final double numericalMean;
    private final double variance;

    public AbstractEnumeratedIntegerDistribution(int n, double[] p) {
        super(IntStream.range(0, n + 1).toArray(), p);

        assert n >= 0;

        numberOfTrials = n;

        probabilities = new double[n + 1];
        cumulativeProbabilities = new double[n + 1];
        for (int i = 0; i <= n; i++) {
            probabilities[i] = this.probability(i);
            cumulativeProbabilities[i] = this.cumulativeProbability(i);
        }

        numericalMean = this.getNumericalMean();
        variance = this.getNumericalVariance();
    }

    public int getNumTrials() {
        return numberOfTrials;
    }

    public double getMean() {
        return numericalMean;
    }

    public double getVariance() {
        return variance;
    }

    public double getProbability(int k) {
        if (k < 0 || k > numberOfTrials) {
            return 0.0D;
        }
        return probabilities[k];
    }

    public double getCumulativeProbability(int k) {
        if (k < 0) {
            return 0.0D;
        }
        if (k > numberOfTrials) {
            return 1.0D;
        }
        return cumulativeProbabilities[k];
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public double[] getCumulativeProbabilities() {
        return cumulativeProbabilities;
    }
}
