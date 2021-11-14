package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import net.minecraft.util.Mth;

public class DiscreteTrapezoidalDistribution extends AbstractDiscreteDistribution {
    public DiscreteTrapezoidalDistribution(double a, double b, double c, double d) {
        super(Mth.ceil(a), buildN(a, d), buildProbabilities(a, b, c, d));
    }

    protected static int buildN(double a, double d) {
        return Mth.floor(d) - Mth.ceil(a) + 1;
    }

    protected static double[] buildProbabilities(double a, double b, double c, double d) {
        TrapezoidalDistribution distribution = new TrapezoidalDistribution(a, b, c, d);

        int n = buildN(a, d);

        double[] probabilities = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            double p = i + Mth.ceil(a);

            probabilities[i] = distribution.probability(p - 0.5D, p + 0.5D);
        }

        return probabilities;
    }
}
