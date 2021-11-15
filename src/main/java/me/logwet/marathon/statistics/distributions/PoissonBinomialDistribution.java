package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import org.apache.commons.math3.complex.Complex;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_binomial_distribution">Poisson binomial
 *     distribution - Wikipedia</a>
 */
public class PoissonBinomialDistribution extends AbstractDiscreteDistribution {
    protected final double[] successProbabilities;

    public PoissonBinomialDistribution(int s, int n, double[] sp) {
        super(s, n, buildProbabilities(s, n, sp));

        assert sp.length == n;
        successProbabilities = sp;
    }

    public PoissonBinomialDistribution(int n, double[] sp) {
        this(0, n, sp);
    }

    /** Discrete Fourier Transform based algorithm. */
    protected static double[] buildProbabilities(int s, int numTrials, double[] sp) {
        int n = numTrials + s;

        final Complex C = Complex.I.multiply(2 * Math.PI).divide(n + 1).exp();

        double[] rArray = new double[numTrials + 1];
        Complex[] productCache = new Complex[n + 1];

        Complex r;

        for (int l = 0; l <= n; l++) {
            r = Complex.ONE;

            for (int m = 1; m <= n; m++) {
                r = r.multiply(C.pow(l).subtract(1).multiply(sp[m - 1]).add(1));
            }

            productCache[l] = r;
        }

        for (int k = s; k <= n; k++) {
            r = Complex.ZERO;

            for (int l = 0; l <= n; l++) {
                r = r.add(productCache[l].multiply(C.pow(-l * k)));
            }

            rArray[k - s] = r.divide(n + 1).getReal();
        }

        return rArray;
    }

    public double[] getSuccessProbabilities() {
        return successProbabilities;
    }
}
