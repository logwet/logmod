package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;
import me.logwet.marathon.statistics.util.AbstractDiscreteDistributionPair;
import me.logwet.marathon.statistics.util.DiscreteDistributionPairBuilder;

public class ElementwiseProductDistribution<
                D1 extends AbstractDiscreteDistribution, D2 extends AbstractDiscreteDistribution>
        extends AbstractDiscreteDistributionPair<D1, D2> {
    public ElementwiseProductDistribution(D1 d1, D2 d2) {
        super(d1, d2, Builder.INSTANCE);
    }

    protected static class Builder implements DiscreteDistributionPairBuilder {
        protected static final Builder INSTANCE = new Builder();

        @Override
        public int buildS(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            return d1.getStartingValue();
        }

        @Override
        public int buildN(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            return d1.getNumTrials();
        }

        @Override
        public double[] buildProbabilities(
                AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            int s = buildS(d1, d2);
            int n = buildN(d1, d2);

            double[] probabilities = new double[n + 1];

            for (int i = s; i <= n; i++) {
                probabilities[i - s] = d1.probability(i) * d2.probability(i);
            }

            return probabilities;
        }
    }
}
