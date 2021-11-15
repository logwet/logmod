package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractContinuousDistribution;
import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;

public class DiscreteDistribution extends AbstractDiscreteDistribution {
    public DiscreteDistribution(int s, int n, double[] p) {
        super(s, n, p);
    }

    public static <T extends AbstractContinuousDistribution> ConvertedDiscreteDistribution<T> from(
            T distribution) {
        return new ConvertedDiscreteDistribution<>(distribution);
    }

    public static <D1 extends AbstractDiscreteDistribution, D2 extends AbstractDiscreteDistribution>
            ElementwiseProductDistribution<D1, D2> elementwiseProductOf(D1 d1, D2 d2) {
        return new ElementwiseProductDistribution<>(d1, d2);
    }
}
