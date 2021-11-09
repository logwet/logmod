package me.logwet.marathon.util.spawner;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.stream.IntStream;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_binomial_distribution">Poisson binomial
 *     distribution - Wikipedia</a>
 */
public class PoissonBinomialDistribution extends EnumeratedIntegerDistribution {
    private int numberOfTrials;

    private double[] successProbabilities;

    private double[] probabilities;
    private double[] cumulativeProbabilities;

    private double numericalMean;
    private double variance;

    public PoissonBinomialDistribution(int[] n, double[] p) {
        super(n, p);
    }

    public PoissonBinomialDistribution(int n, double[] p) {
        this(IntStream.range(0, n + 1).toArray(), buildProbabilities(n, p));

        assert n >= 0;
        assert p.length >= n;

        numberOfTrials = n;
        successProbabilities = p;

        probabilities = new double[n + 1];
        cumulativeProbabilities = new double[n + 1];
        for (int i = 0; i <= n; i++) {
            probabilities[i] = this.probability(i);
            cumulativeProbabilities[i] = this.cumulativeProbability(i);
        }

        numericalMean = this.getNumericalMean();
        variance = this.getNumericalVariance();
    }

    /** Discrete Fourier Transform based algorithm. */
    private static double[] buildProbabilities(int n, double[] successProbabilities) {
        final Complex C = Complex.I.multiply(2 * Math.PI).divide(n + 1).exp();

        double[] rArray = new double[n + 1];
        Complex[] productCache = new Complex[n + 1];

        Complex r;

        for (int l = 0; l <= n; l++) {
            r = Complex.ONE;

            for (int m = 1; m <= n; m++) {
                r = r.multiply(C.pow(l).subtract(1).multiply(successProbabilities[m - 1]).add(1));
            }

            productCache[l] = r;
        }

        for (int k = 0; k <= n; k++) {
            r = Complex.ZERO;

            for (int l = 0; l <= n; l++) {
                r = r.add(productCache[l].multiply(C.pow(-l * k)));
            }

            rArray[k] = r.divide(n + 1).getReal();
        }

        return rArray;
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

    public double[] getSuccessProbabilities() {
        return successProbabilities;
    }
}
