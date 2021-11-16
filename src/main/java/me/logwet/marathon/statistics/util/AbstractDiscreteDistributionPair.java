package me.logwet.marathon.statistics.util;

public abstract class AbstractDiscreteDistributionPair<
                D1 extends AbstractDiscreteDistribution, D2 extends AbstractDiscreteDistribution>
        extends AbstractDiscreteDistribution {
    protected final D1 d1;
    protected final D2 d2;

    public AbstractDiscreteDistributionPair(D1 d1, D2 d2, DiscreteDistributionPairBuilder builder) {
        super(builder.buildS(d1, d2), builder.buildN(d1, d2), builder.buildProbabilities(d1, d2));

        this.d1 = d1;
        this.d2 = d2;
    }

    public D1 getD1() {
        return d1;
    }

    public D2 getD2() {
        return d2;
    }
}
