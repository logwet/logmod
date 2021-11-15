package me.logwet.marathon.statistics.util;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.stream.IntStream;

public abstract class AbstractDiscreteDistribution extends EnumeratedIntegerDistribution {
    protected final int startingValue;
    protected final int numberOfTrials;

    protected final double[] probabilities;
    protected final double[] cumulativeProbabilities;

    protected final double numericalMean;
    protected final double variance;

    public AbstractDiscreteDistribution(int s, int n, double[] p) {
        super(IntStream.range(s, s + n + 1).toArray(), trimArray(n, p));

        assert n >= 0;

        startingValue = s;
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

    protected static double[] trimArray(int n, double[] a) {
        assert a.length >= n;

        double[] r = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            r[i] = a[i];
        }

        return r;
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
