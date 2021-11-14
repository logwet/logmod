package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractEnumeratedIntegerDistribution;
import me.logwet.marathon.statistics.util.AbstractProductOfEnumeratedIntegerDistributions;

public class ProductOfEnumeratedIntegerDistributionsImpl
        extends AbstractProductOfEnumeratedIntegerDistributions {
    public ProductOfEnumeratedIntegerDistributionsImpl(
            AbstractEnumeratedIntegerDistribution d1, AbstractEnumeratedIntegerDistribution d2) {
        super(d1, d2);
    }
}
