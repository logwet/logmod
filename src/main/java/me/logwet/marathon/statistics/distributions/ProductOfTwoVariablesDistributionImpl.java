package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractEnumeratedIntegerDistribution;
import me.logwet.marathon.statistics.util.AbstractProductOfTwoVariablesDistribution;

public class ProductOfTwoVariablesDistributionImpl
        extends AbstractProductOfTwoVariablesDistribution {
    public ProductOfTwoVariablesDistributionImpl(
            AbstractEnumeratedIntegerDistribution d1, AbstractEnumeratedIntegerDistribution d2) {
        super(d1, d2);
    }
}
