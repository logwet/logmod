package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractContinuousDistribution;
import net.minecraft.util.Mth;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Inverse_distribution">Inverse distribution -
 *     Wikipedia</a>
 */
public class InverseUniformDistribution extends AbstractContinuousDistribution {
    protected double preLb;
    protected double preUb;
    protected double preSupport;
    protected double fac;

    public InverseUniformDistribution(double lower, double upper, double factor)
            throws NumberIsTooLargeException {
        super();

        if (lower >= upper) {
            throw new NumberIsTooLargeException(
                    LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
        }

        preLb = lower;
        preUb = upper;
        preSupport = preUb - preLb;

        assert factor > 0.0D;

        fac = factor;
    }

    @Override
    public double density(double x) {
        if (x < getSupportLowerBound() || x > getSupportUpperBound()) {
            return 0.0;
        }

        return fac / (preSupport * x * x);
    }

    @Override
    public double cumulativeProbability(double x) {
        if (x < getSupportLowerBound()) {
            return 0;
        }
        if (x > getSupportUpperBound()) {
            return 1;
        }

        return Mth.clamp((preUb - (fac / x)) / preSupport, 0.0D, 1.0D);
    }

    protected double standardMean() {
        return (Math.log(preUb) - Math.log(preLb)) / preSupport;
    }

    @Override
    public double getNumericalMean() {
        return fac * standardMean();
    }

    protected double standardVariance() {
        return (1.0D / preLb * preUb) - square(standardMean());
    }

    @Override
    public double getNumericalVariance() {
        return fac * fac * standardVariance();
    }

    public double getStandardDeviation() {
        return fac * Math.sqrt(standardVariance());
    }

    @Override
    public double getSupportLowerBound() {
        return 1.0D / preUb;
    }

    @Override
    public double getSupportUpperBound() {
        return 1.0D / preLb;
    }

    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }

    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
