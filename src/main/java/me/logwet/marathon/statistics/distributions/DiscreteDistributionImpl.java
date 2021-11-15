package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractContinuousDistribution;
import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import net.minecraft.util.Mth;

public class DiscreteDistributionImpl extends AbstractDiscreteDistribution {
    public DiscreteDistributionImpl(int s, int n, double[] p) {
        super(s, n, p);
    }

    public DiscreteDistributionImpl(AbstractContinuousDistribution distribution) {
        this(buildS(distribution), buildN(distribution), buildProbabilities(distribution));
    }

    protected static int buildS(AbstractContinuousDistribution distribution) {
        return Mth.ceil(distribution.getSupportLowerBound());
    }

    protected static int buildN(AbstractContinuousDistribution distribution) {
        return Mth.floor(distribution.getSupportUpperBound())
                - Mth.ceil(distribution.getSupportLowerBound())
                + 1;
    }

    protected static double[] buildProbabilities(AbstractContinuousDistribution distribution) {
        int s = buildS(distribution);
        int n = buildN(distribution);

        double[] probabilities = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            double p = i + s;

            probabilities[i] = distribution.probability(p - 0.5D, p + 0.5D);
        }

        return probabilities;
    }
}
