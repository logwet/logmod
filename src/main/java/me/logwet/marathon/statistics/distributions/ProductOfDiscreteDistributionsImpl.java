package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import me.logwet.marathon.statistics.util.AbstractProductOfDiscreteDistributions;

public class ProductOfDiscreteDistributionsImpl extends AbstractProductOfDiscreteDistributions {
    public ProductOfDiscreteDistributionsImpl(
            AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        super(d1, d2);
    }
}
