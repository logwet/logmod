package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;

public class EnumeratedIntegerDistributionImpl extends AbstractDiscreteDistribution {
    public EnumeratedIntegerDistributionImpl(int s, int n, double[] p) {
        super(s, n, p);
    }
}
