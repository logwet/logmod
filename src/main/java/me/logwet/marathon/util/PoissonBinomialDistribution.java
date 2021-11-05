package me.logwet.marathon.util;

/**
 * Algorithm translated from mathematical notation to pseudocode by al, and implemented in Java by
 * logwet.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_binomial_distribution">Poisson binomial
 *     distribution - Wikipedia</a>
 * @author logwet & al
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

    private double T(double i) {
        double r = 0D;

        for (int j = 1; j <= numberOfTrials; j++) {
            r += Math.pow((successProbabilities[j - 1] / (1 - successProbabilities[j - 1])), i);
        }

        return r;
    }

    /**
     * Implementation of the recursive formula but past values are cached so it isn't actually
     * recursive. This fails if any of the success probabilities is equal to 1, so I may switch to
     * the discrete Fourier transform implementation at some point.
     */
    private double[] buildProbabilities() {
        for (int i = 0; i < numberOfTrials; i++) {
            assert successProbabilities[i] < 1D;
        }

        double[] rArray = new double[numberOfTrials + 1];

        double[] tArray = new double[numberOfTrials];
        for (int h = 1; h <= numberOfTrials; h++) {
            tArray[h - 1] = T(h);
        }

        double r;

        for (int k = 0; k <= numberOfTrials; k++) {
            if (k == 0) {
                r = 1D;

                for (int i = 1; i <= numberOfTrials; i++) {
                    r *= (1 - successProbabilities[i - 1]);
                }

                rArray[k] = r;
            } else {
                r = 0D;

                for (int i = 1; i <= k; i++) {
                    r += Math.pow(-1, i - 1) * rArray[k - i] * tArray[i - 1];
                }

                rArray[k] = r / k;
            }
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
