package me.logwet.logmod.statistics.distributions;

import me.logwet.logmod.statistics.util.AbstractContinuousDistribution;
import me.logwet.logmod.statistics.util.AbstractDiscreteDistribution;
import net.minecraft.util.Mth;

public class ConvertedDiscreteDistribution<T extends AbstractContinuousDistribution>
        extends AbstractDiscreteDistribution {
    protected final T sourceDistribution;

    public ConvertedDiscreteDistribution(T distribution) {
        super(buildS(distribution), buildN(distribution), buildProbabilities(distribution));

        sourceDistribution = distribution;
    }

    protected static int buildS(AbstractContinuousDistribution distribution) {
        return Mth.ceil(distribution.getSupportLowerBound());
    }

    protected static int buildN(AbstractContinuousDistribution distribution) {
        return Mth.floor(distribution.getSupportUpperBound())
                - Mth.ceil(distribution.getSupportLowerBound());
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

    public T getSourceDistribution() {
        return sourceDistribution;
    }
}
