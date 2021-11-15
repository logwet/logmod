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
        int n1 = d1.getNumTrials();
        int n2 = d2.getNumTrials();

        double[] probabilities = new double[n + 1];

        AbstractDiscreteDistribution td1 = n1 <= n2 ? d1 : d2;
        AbstractDiscreteDistribution td2 = n1 <= n2 ? d2 : d1;

        for (int k = 0; k <= n; k++) {
            if (k == 0) {
                for (int t1 = td1.getSupportLowerBound(); t1 <= td1.getSupportUpperBound(); t1++) {
                    probabilities[0] += td1.probability(t1) * td2.probability(0);
                }
                for (int t2 = td2.getSupportLowerBound(); t2 <= td2.getSupportUpperBound(); t2++) {
                    probabilities[0] += td1.probability(0) * td2.probability(t2);
                }
            } else {
                for (int t1 = td1.getSupportLowerBound(); t1 <= td1.getSupportUpperBound(); t1++) {
                    if (t1 != 0 && k % t1 == 0) {
                        probabilities[k] += td1.probability(t1) * td2.probability(k / t1);
                    }
                }
            }
        }

        return probabilities;
    }
}
