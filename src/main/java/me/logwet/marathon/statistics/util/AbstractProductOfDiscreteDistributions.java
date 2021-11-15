package me.logwet.marathon.statistics.util;

public abstract class AbstractProductOfDiscreteDistributions extends AbstractDiscreteDistribution {
    protected final AbstractDiscreteDistribution d1;
    protected final AbstractDiscreteDistribution d2;

    public AbstractProductOfDiscreteDistributions(
            AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        super(0, buildN(d1, d2), buildProbabilities(d1, d2));

        this.d1 = d1;
        this.d2 = d2;
    }

    protected static int buildN(AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        return d1.getSupportUpperBound() * d2.getSupportUpperBound();
    }

    protected static double[] buildProbabilities(
            AbstractDiscreteDistribution d1, AbstractDiscreteDistribution d2) {
        int n = buildN(d1, d2);
        int n1 = d1.numberOfTrials;
        int n2 = d2.numberOfTrials;

        double[] probabilities = new double[n + 1];

        AbstractDiscreteDistribution td1 = n1 <= n2 ? d1 : d2;
        AbstractDiscreteDistribution td2 = n1 <= n2 ? d2 : d1;
        int tn1 = Math.min(n1, n2);
        int tn2 = Math.max(n1, n2);

        for (int k = 0; k <= n; k++) {
            for (int t = td1.startingValue; t <= tn1; t++) {
                try {
                    if (k % t == 0) {
                        probabilities[k] += td1.getProbability(t) * td2.getProbability(k / t);
                    }
                } catch (ArithmeticException ignored) {
                    for (int t2 = td2.startingValue; t2 <= tn2; t2++) {
                        probabilities[k] += td1.getProbability(0) * td2.getProbability(t2);
                    }
                }
            }
        }

        return probabilities;
    }
}
