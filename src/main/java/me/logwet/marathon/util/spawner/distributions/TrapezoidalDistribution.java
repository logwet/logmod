package me.logwet.marathon.util.spawner.distributions;

import org.apache.commons.lang3.Range;

import java.util.function.Function;

public class TrapezoidalDistribution extends PiecewiseDistribution {
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

    private static PiecewiseFunction<Double, Double> buildPDF(
            double a, double b, double c, double d) {
        double fac = 2.0D / (a + b - c - d);

        PiecewiseFunction<Double, Double> pdf = new PiecewiseFunction<>();
        pdf.addPiece(Range.between(a, b), (x) -> fac * ((x - a) / (a - b)));
        pdf.addPiece(Range.between(b, c), (x) -> -fac);
        pdf.addPiece(Range.between(c, d), (x) -> -fac * ((x - d) * (c - d)));

        return pdf;
    }

    private static PiecewiseFunction<Double, Double> buildCDF(
            double a, double b, double c, double d) {
        double fac = a + b - c - d;

        Function<Double, Double> square = (x) -> x * x;

        PiecewiseFunction<Double, Double> cdf = new PiecewiseFunction<>();
        cdf.addPiece(Range.between(a, b), (x) -> square.apply(x - a) / (fac * (a - b)));
        cdf.addPiece(Range.between(b, c), (x) -> -(2.0D * x - a - b) / fac);
        cdf.addPiece(Range.between(c, d), (x) -> 1.0D - (square.apply(x - d) / (fac * (c - d))));

        return cdf;
    }

    private static PiecewiseFunction<Double, Double> buildICDF(
            double a, double b, double c, double d) {
        double fac = a + b - c - d;

        Function<Double, Double> mid = (x) -> -(2.0D * x - a - b) / fac;
        double s1 = mid.apply(b);
        double s2 = mid.apply(c);

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
        double mean = getNumericalMean();

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
        return B;
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
