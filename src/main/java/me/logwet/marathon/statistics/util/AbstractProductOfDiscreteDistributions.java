package me.logwet.marathon.statistics.util;

public class AbstractProductOfDiscreteDistributions<
                D1 extends AbstractDiscreteDistribution, D2 extends AbstractDiscreteDistribution>
        extends AbstractDiscreteDistributionPair<D1, D2> {
    public AbstractProductOfDiscreteDistributions(D1 d1, D2 d2) {
        super(d1, d2, Builder.INSTANCE);
    }

    protected static class Builder implements DiscreteDistributionPairBuilder {
        protected static final Builder INSTANCE = new Builder();

        @Override
        public int buildS(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            return 0;
        }

        @Override
        public int buildN(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            return d1.getSupportUpperBound() * d2.getSupportUpperBound();
        }

        @Override
        public double[] buildProbabilities(
                AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
            int n = buildN(d1, d2);
            int n1 = d1.getNumTrials();
            int n2 = d2.getNumTrials();

            double[] probabilities = new double[n + 1];

            AbstractDiscreteDistribution td1 = n1 <= n2 ? d1 : d2;
            AbstractDiscreteDistribution td2 = n1 <= n2 ? d2 : d1;

            for (int t = td1.getSupportLowerBound(); t <= td1.getSupportUpperBound(); t++) {
                probabilities[0] += td1.probability(t) * td2.probability(0);
            }
            for (int t = td2.getSupportLowerBound(); t <= td2.getSupportUpperBound(); t++) {
                probabilities[0] += td1.probability(0) * td2.probability(t);
            }

            for (int k = 1; k <= n; k++) {
                for (int t = td1.getSupportLowerBound(); t <= td1.getSupportUpperBound(); t++) {
                    if (t != 0 && k % t == 0) {
                        probabilities[k] += td1.probability(t) * td2.probability(k / t);
                    }
                }
            }

            return probabilities;
        }
    }
}
