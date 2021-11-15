package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import me.logwet.marathon.statistics.util.AbstractProductOfDiscreteDistributions;

public class ProductOfThreeDiscreteDistributions extends AbstractProductOfDiscreteDistributions {
    public ProductOfThreeDiscreteDistributions(
            AbstractDiscreteDistribution d1,
            AbstractDiscreteDistribution d2,
            AbstractDiscreteDistribution d3) {
        super(new ProductOfTwoDiscreteDistributions(d1, d2), d3);
    }
}
