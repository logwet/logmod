package me.logwet.logmod.statistics.util;

public interface DiscreteDistributionPairBuilder {
    int buildS(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2);

    int buildN(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2);

    double[] buildProbabilities(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2);
}
