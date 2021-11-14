package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import net.minecraft.util.Mth;

public class DiscreteInverseUniformDistribution extends AbstractDiscreteDistribution {
    public DiscreteInverseUniformDistribution(double lower, double upper, double factor) {
        super(
                Mth.ceil(factor / upper),
                buildN(lower, upper, factor),
                buildProbabilities(lower, upper, factor));
    }

    protected static int buildN(double lower, double upper, double factor) {
        return Mth.floor(factor / lower) - Mth.ceil(factor / upper) + 1;
    }

    protected static double[] buildProbabilities(double lower, double upper, double factor) {
        InverseUniformDistribution distribution =
                new InverseUniformDistribution(lower, upper, factor);

        int n = buildN(lower, upper, factor);

        double l = factor / upper;
        double u = factor / lower;

        double[] probabilities = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            double p = i + Mth.ceil(l);

            probabilities[i] = distribution.probability(p - 0.5D, p + 0.5D);
        }

        return probabilities;
    }
}
