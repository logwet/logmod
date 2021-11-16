package me.logwet.logmod.statistics.util;

import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.util.MathArrays;

import java.util.stream.IntStream;

public abstract class AbstractDiscreteDistribution extends EnumeratedIntegerDistribution {
    protected final int startingValue;
    protected final int numberOfTrials;

    protected final double[] probabilities;
    protected final double[] cumulativeProbabilities;

    protected final int lb;
    protected final int ub;
    protected final double numericalMean;
    protected final double variance;

    public AbstractDiscreteDistribution(int s, int n, double[] p) {
        super(IntStream.range(s, s + n + 1).toArray(), trimArray(n, p));

        assert n >= 0;

        lb = super.getSupportLowerBound();
        ub = super.getSupportUpperBound();

        numericalMean = super.getNumericalMean();
        variance = super.getNumericalVariance();

        startingValue = lb;
        numberOfTrials = ub - lb;

        probabilities =
                MathArrays.normalizeArray(trimArray(startingValue, numberOfTrials, lb, p), 1.0);

        cumulativeProbabilities = new double[numberOfTrials + 1];
        double sum = 0.0D;

        for (int i = 0; i <= numberOfTrials; i++) {
            sum += probabilities[i];
            cumulativeProbabilities[i] = Mth.clamp(sum, 0.0D, 1.0D);
        }
    }

    protected static double[] trimArray(int s, int n, int lb, double[] a) {
        double[] r = new double[n + 1];

        System.arraycopy(a, lb - s, r, 0, n + 1);

        return r;
    }

    protected static double[] trimArray(int n, double[] a) {
        assert a.length >= n;

        double[] r = new double[n + 1];

        System.arraycopy(a, 0, r, 0, n + 1);

        return r;
    }

    public int getStartingValue() {
        return startingValue;
    }

    public int getNumTrials() {
        return numberOfTrials;
    }

    @Override
    public double getNumericalMean() {
        return numericalMean;
    }

    @Override
    public double getNumericalVariance() {
        return variance;
    }

    @Override
    public double probability(int k) {
        if (k < this.getSupportLowerBound() || k > this.getSupportUpperBound()) {
            return 0.0D;
        }
        return probabilities[k - startingValue];
    }

    @Override
    public double cumulativeProbability(int k) {
        if (k < this.getSupportLowerBound()) {
            return 0.0D;
        }
        if (k > this.getSupportUpperBound()) {
            return 1.0D;
        }

        return cumulativeProbabilities[k - startingValue];
    }

    @Override
    public int getSupportLowerBound() {
        return lb;
    }

    @Override
    public int getSupportUpperBound() {
        return ub;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public double[] getCumulativeProbabilities() {
        return cumulativeProbabilities;
    }
}
