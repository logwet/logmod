package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractPiecewiseDistribution;
import me.logwet.marathon.statistics.util.PiecewiseFunction;
import org.apache.commons.lang3.Range;

import java.util.function.Function;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Trapezoidal_distribution">Trapezoidal distribution -
 *     Wikipedia</a>
 */
public class TrapezoidalDistribution extends AbstractPiecewiseDistribution {
    private final double A;
    private final double B;
    private final double C;
    private final double D;

    public TrapezoidalDistribution(double a, double b, double c, double d) {
        super(buildPDF(a, b, c, d), buildCDF(a, b, c, d), buildICDF(a, b, c, d));

        assert a <= b && b <= c && c <= d;

        A = a;
        B = b;
        C = c;
        D = d;
    }

    protected static PiecewiseFunction<Double, Double> buildPDF(
            double a, double b, double c, double d) {
        final double fac = 2.0D / (a + b - c - d);

        PiecewiseFunction<Double, Double> pdf = new PiecewiseFunction<>();
        pdf.addPiece(Range.between(a, b), (x) -> fac * ((x - a) / (a - b)));
        pdf.addPiece(Range.between(b, c), (x) -> -fac);
        pdf.addPiece(Range.between(c, d), (x) -> -fac * ((x - d) * (c - d)));

        return pdf;
    }

    protected static PiecewiseFunction<Double, Double> buildCDF(
            double a, double b, double c, double d) {
        final double fac = a + b - c - d;

        PiecewiseFunction<Double, Double> cdf = new PiecewiseFunction<>();
        cdf.addPiece(Range.between(a, b), (x) -> square(x - a) / (fac * (a - b)));
        cdf.addPiece(Range.between(b, c), (x) -> -(2.0D * x - a - b) / fac);
        cdf.addPiece(Range.between(c, d), (x) -> 1.0D - (square(x - d) / (fac * (c - d))));

        return cdf;
    }

    protected static PiecewiseFunction<Double, Double> buildICDF(
            double a, double b, double c, double d) {
        final double fac = a + b - c - d;

        Function<Double, Double> mid = (x) -> -(2.0D * x - a - b) / fac;
        final double s1 = mid.apply(b);
        final double s2 = mid.apply(c);

        PiecewiseFunction<Double, Double> icdf = new PiecewiseFunction<>();
        icdf.addPiece(Range.between(0.0D, s1), (x) -> Math.sqrt((a - b) * fac * x) + a);
        icdf.addPiece(Range.between(s1, s2), (x) -> -(fac * x - a - b) / 2.0D);
        icdf.addPiece(Range.between(s2, 1.0D), (x) -> -Math.sqrt(-fac * (c - d) * (x - 1.0D)) + d);

        return icdf;
    }

    @Override
    public double getNumericalMean() {
        return (1.0D / (3.0D * (D + C - B - A)))
                * (((D * D * D - C * C * C) / (D - C)) - ((B * B * B - A * A * A) / (B - A)));
    }

    @Override
    public double getNumericalVariance() {
        final double mean = getNumericalMean();

        return (1.0D / (6.0D * (D + C - B - A)))
                        * (((D * D * D * D - C * C * C * C) / (D - C))
                                - ((B * B * B * B - A * A * A * A) / (B - A)))
                - mean * mean;
    }

    @Override
    public double getSupportLowerBound() {
        return A;
    }

    @Override
    public double getSupportUpperBound() {
        return D;
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
