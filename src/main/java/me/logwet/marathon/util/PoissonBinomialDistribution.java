package me.logwet.marathon.util;

import org.apache.commons.math3.complex.Complex;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_binomial_distribution">Poisson binomial
 *     distribution - Wikipedia</a>
 */
public class PoissonBinomialDistribution {
    private final int numberOfTrials;
    private final double[] successProbabilities;
    private final double[] probabilities;

    public PoissonBinomialDistribution(int n, double[] p) {
        assert n >= 0;
        assert p.length >= n;

        numberOfTrials = n;
        successProbabilities = p;

        probabilities = buildProbabilities();
    }

    /** Discrete Fourier Transform based algorithm. */
    private double[] buildProbabilities() {
        double[] rArray = new double[numberOfTrials + 1];
        final Complex C = Complex.I.multiply(2 * Math.PI).divide(numberOfTrials + 1).exp();

        Complex[] productCache = new Complex[numberOfTrials + 1];

        Complex r;

        for (int l = 0; l <= numberOfTrials; l++) {
            r = Complex.ONE;

            for (int m = 1; m <= numberOfTrials; m++) {
                r = r.multiply(C.pow(l).subtract(1).multiply(successProbabilities[m - 1]).add(1));
            }

            productCache[l] = r;
        }

        for (int k = 0; k <= numberOfTrials; k++) {
            r = Complex.ZERO;

            for (int l = 0; l <= numberOfTrials; l++) {
                r = r.add(productCache[l].multiply(C.pow(-l * k)));
            }

            rArray[k] = r.divide(numberOfTrials + 1).getReal();
        }

        return rArray;
    }

    public double getNumericalMean() {
        double r = 0D;

        for (int i = 1; i <= this.numberOfTrials; i++) {
            r += successProbabilities[i - 1];
        }

        return r;
    }

    public double getProbability(int k) {
        if (k < 0 || k > numberOfTrials) {
            return 0D;
        }
        return probabilities[k];
    }

    public double[] getProbabilities() {
        return probabilities;
    }
}
