package me.logwet.marathon.statistics.util;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

public abstract class AbstractContinuousDistribution extends AbstractRealDistribution {
    public AbstractContinuousDistribution() {
        super(new JDKRandomGenerator());
    }

    protected static int signFromPow(int k) {
        return (k & 1) == 0 ? 1 : -1;
    }

    protected static double square(double x) {
        return x * x;
    }

    protected static double cube(double x) {
        return x * x * x;
    }

    protected static double fourth(double x) {
        return x * x * x * x;
    }
}
