package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractEnumeratedIntegerDistribution;

public class DiscreteTrapezoidalDistribution extends AbstractEnumeratedIntegerDistribution {
    public DiscreteTrapezoidalDistribution(int n, double a, double b, double c, double d) {
        super(n, buildProbabilities(n, a, b, c, d));
    }

    protected static double[] buildProbabilities(int n, double a, double b, double c, double d) {
        TrapezoidalDistribution distribution = new TrapezoidalDistribution(a, b, c, d);

        double[] probabilities = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            double p1 = ((double) i / (double) n) * (d - a) + a;
            double p2 = ((d - a) / (double) n) / 2.0D;

            probabilities[i] = distribution.probability(p1 - p2, p1 + p2);
        }

        return probabilities;
    }
}
