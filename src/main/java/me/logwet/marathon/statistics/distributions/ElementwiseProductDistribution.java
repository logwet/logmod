package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractDiscreteDistribution;

public class ElementwiseProductDistribution<
                D1 extends AbstractDiscreteDistribution, D2 extends AbstractDiscreteDistribution>
        extends AbstractDiscreteDistribution {
    protected D1 d1;
    protected D2 d2;

    public ElementwiseProductDistribution(D1 d1, D2 d2) {
        super(buildS(d1, d2), buildN(d1, d2), buildProbabilities(d1, d2));

        this.d1 = d1;
        this.d2 = d2;
    }

    private static int buildS(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        return d1.getStartingValue();
    }

    private static int buildN(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        return d1.getNumTrials();
    }

    private static double[] buildProbabilities(
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
