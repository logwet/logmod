package me.logwet.marathon.util.spawner.distributions;

import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Irwin%E2%80%93Hall_distribution">Irwin-Hall
 *     distribution - Wikipedia</a>
 */
public class IrwinHallDistribution extends AbstractRealDistribution {
    private static final double SQRT_12 = 2 * Math.sqrt(3);

    private final int n;

    private final double preLB;
    private final double preUB;

    private final double lb;
    private final double ub;

    private final double preSupport;
    private final double support;

    private final long P_F;
    private final long C_F;

    private final long[] numCache;

    public IrwinHallDistribution(
            int iterations, double min, double max, RandomGenerator randomGenerator) {
        super(randomGenerator);
        n = iterations;

        assert n > 0 && n <= 20;
        assert max > min;

        preLB = Math.min(min, max);
        preUB = Math.max(max, min);

        lb = preLB * n;
        ub = preUB * n;

        preSupport = preUB - preLB;
        support = ub - lb;

        P_F = CombinatoricsUtils.factorial(n - 1);
        C_F = CombinatoricsUtils.factorial(n);

        numCache = new long[n + 1];
        long[] nCkCache = new long[1 + n / 2];

        long nCk = 1;
        for (int k = 0; k <= n; k++) {
            numCache[k] = signFromPow(k) * nCk;

            if (k > n / 2) {
                nCk = nCkCache[n - k];
            } else {
                nCkCache[k] = nCk;
                nCk = nCk * (n - k) / (k + 1);
            }
        }
    }

    public IrwinHallDistribution(int iterations, double min, double max) {
        this(iterations, min, max, new JDKRandomGenerator());
    }

    public IrwinHallDistribution(int iterations) {
        this(iterations, 0.0D, 1.0D);
    }

    private static int signFromPow(int k) {
        return (k & 1) == 0 ? 1 : -1;
    }

    private double transformToStandardSpace(double x) {
        return (x - lb) / preSupport;
    }

    private double transformFromStandardSpace(double x) {
        return x * preSupport + lb;
    }

    public double scaleAndClamp(double x) {
        return Mth.clamp(x, preLB, preUB) * n;
    }

    @Override
    public double density(double x) {
        if (x < lb || x > ub) {
            return 0.0D;
        }

        x = transformToStandardSpace(x);

        double r = 0.0D;

        for (int k = 0; k <= Mth.floor(x); k++) {
            r += numCache[k] * Math.pow(x - k, n - 1);
        }

        return Mth.clamp(r / P_F, 0.0D, 1.0D);
    }

    @Override
    public double cumulativeProbability(double x) {
        if (x < lb) {
            return 0.0D;
        } else if (x > ub) {
            return 1.0D;
        }

        x = transformToStandardSpace(x);

        double r = 0.0D;

        for (int k = 0; k <= Mth.floor(x); k++) {
            r += numCache[k] * Math.pow(x - k, n);
        }

        return Mth.clamp(r / C_F, 0.0D, 1.0D);
    }

    @Override
    public double getNumericalMean() {
        return (ub + lb) / 2.0D;
    }

    @Override
    public double getNumericalVariance() {
        return preSupport * preSupport * n / 12.0D;
    }

    public double getStandardDeviation() {
        return preSupport * Math.sqrt(n) / SQRT_12;
    }

    @Override
    public double getSupportLowerBound() {
        return lb;
    }

    @Override
    public double getSupportUpperBound() {
        return ub;
    }

    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }

    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
