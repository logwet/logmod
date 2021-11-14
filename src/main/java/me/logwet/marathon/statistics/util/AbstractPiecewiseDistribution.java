package me.logwet.marathon.statistics.util;

import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractPiecewiseDistribution extends AbstractRealDistribution {
    protected final PiecewiseFunction<Double, Double> PDF;
    protected final PiecewiseFunction<Double, Double> CDF;

    @Nullable protected final PiecewiseFunction<Double, Double> ICDF;

    public AbstractPiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF,
            PiecewiseFunction<Double, Double> CDF,
            @Nullable PiecewiseFunction<Double, Double> ICDF,
            RandomGenerator rng) {
        super(rng);
        this.PDF = PDF;
        this.CDF = CDF;
        this.ICDF = ICDF;
    }

    public AbstractPiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF,
            PiecewiseFunction<Double, Double> CDF,
            @Nullable PiecewiseFunction<Double, Double> ICDF) {
        this(PDF, CDF, ICDF, new JDKRandomGenerator());
    }

    public AbstractPiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF, PiecewiseFunction<Double, Double> CDF) {
        this(PDF, CDF, null);
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

    protected double uncheckedDensity(double x) {
        Double r = PDF.apply(x);

        if (!Objects.isNull(r)) {
            return r;
        } else {
            return 0.0D;
        }
    }

    @Override
    public double density(double x) {
        if (x < this.getSupportLowerBound() || x > this.getSupportUpperBound()) {
            return 0.0D;
        }

        return uncheckedDensity(x);
    }

    protected double uncheckedCumulativeProbability(double x) {
        Double r = CDF.apply(x);

        if (!Objects.isNull(r)) {
            return Mth.clamp(r, 0.0D, 1.0D);
        } else {
            return 0.0D;
        }
    }

    @Override
    public double cumulativeProbability(double x) {
        if (x < this.getSupportLowerBound()) {
            return 0.0D;
        } else if (x > this.getSupportUpperBound()) {
            return 1.0D;
        }

        return uncheckedCumulativeProbability(x);
    }

    @Override
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (!Objects.isNull(ICDF)) {
            Double r = ICDF.apply(p);
            if (!Objects.isNull(r)) {
                return r;
            }
        }

        return defaultInverseCumulativeProbability(p);
    }

    public double defaultInverseCumulativeProbability(double p) throws OutOfRangeException {
        return super.inverseCumulativeProbability(p);
    }
}
