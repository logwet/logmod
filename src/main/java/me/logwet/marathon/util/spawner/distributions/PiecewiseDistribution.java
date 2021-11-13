package me.logwet.marathon.util.spawner.distributions;

import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class PiecewiseDistribution extends AbstractRealDistribution {
    protected final PiecewiseFunction<Double, Double> PDF;
    protected final PiecewiseFunction<Double, Double> CDF;

    @Nullable protected final PiecewiseFunction<Double, Double> ICDF;

    public PiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF,
            PiecewiseFunction<Double, Double> CDF,
            @Nullable PiecewiseFunction<Double, Double> ICDF,
            RandomGenerator rng) {
        super(rng);
        this.PDF = PDF;
        this.CDF = CDF;
        this.ICDF = ICDF;
    }

    public PiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF,
            PiecewiseFunction<Double, Double> CDF,
            @Nullable PiecewiseFunction<Double, Double> ICDF) {
        this(PDF, CDF, ICDF, new JDKRandomGenerator());
    }

    public PiecewiseDistribution(
            PiecewiseFunction<Double, Double> PDF, PiecewiseFunction<Double, Double> CDF) {
        this(PDF, CDF, null);
    }

    @Override
    public double density(double x) {
        if (x < this.getSupportLowerBound() || x > this.getSupportUpperBound()) {
            return 0.0D;
        }

        Double r = PDF.apply(x);

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

        Double r = CDF.apply(x);

        if (!Objects.isNull(r)) {
            return Mth.clamp(r, 0.0D, 1.0D);
        } else {
            return 0.0D;
        }
    }

    @Override
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (!Objects.isNull(ICDF)) {
            Double r = ICDF.apply(p);
            if (!Objects.isNull(r)) {
                return r;
            }
        }

        return super.inverseCumulativeProbability(p);
    }
}
