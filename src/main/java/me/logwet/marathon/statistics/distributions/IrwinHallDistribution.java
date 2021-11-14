package me.logwet.marathon.statistics.distributions;

import me.logwet.marathon.statistics.util.AbstractPiecewiseDistribution;
import me.logwet.marathon.statistics.util.PiecewiseFunction;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.function.Function;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Irwin%E2%80%93Hall_distribution">Irwin-Hall
 *     distribution - Wikipedia</a>
 */
public class IrwinHallDistribution extends AbstractPiecewiseDistribution {
    private static final double INV_SQRT_12 = 0.5D * Mth.fastInvSqrt(3.0D);

    private final int n;

    private final double preLB;
    private final double preUB;

    private final double lb;
    private final double ub;

    private final double preSupport;
    private final double support;

    private long P_F;
    private long C_F;

    private long[] numCache;

    public IrwinHallDistribution(int iterations, double min, double max) {
        super(buildPDF(iterations), buildCDF(iterations), buildICDF(iterations));

        n = iterations;

        assert n > 0 && n <= 20;
        assert max > min;

        preLB = min;
        preUB = max;

        lb = preLB * n;
        ub = preUB * n;

        preSupport = preUB - preLB;
        support = ub - lb;

        if (n > 5) {
            P_F = CombinatoricsUtils.factorial(n - 1);
            C_F = CombinatoricsUtils.factorial(n);

            numCache = new long[n + 1];
            long[] nCkCache = new long[1 + n / 2];

            long nCk = 1;
            for (int k = 0; k <= n; k++) {
                numCache[k] = signFromPow(k) * nCk;

                if (k > n / 2) {
                    nCk = nCkCache[n - k];
                } else {
                    nCkCache[k] = nCk;
                    nCk = nCk * (n - k) / (k + 1);
                }
            }
        }
    }

    public IrwinHallDistribution(int iterations) {
        this(iterations, 0.0D, 1.0D);
    }

    private static int signFromPow(int k) {
        return (k & 1) == 0 ? 1 : -1;
    }

    protected static PiecewiseFunction<Double, Double> buildPDF(int n) {
        PiecewiseFunction<Double, Double> pdf = new PiecewiseFunction<>();

        switch (n) {
            case 1:
                pdf.addPiece(Range.between(0.0D, 1.0D), (x) -> 1.0D);
                break;

            case 2:
                pdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x);
                pdf.addPiece(Range.between(1.0D, 2.0D), (x) -> 2.0D - x);
                break;

            case 3:
                pdf.addPiece(Range.between(0.0D, 1.0D), (x) -> 0.5D * x * x);
                pdf.addPiece(Range.between(1.0D, 2.0D), (x) -> -x * x + 3.0D * x - 1.5D);
                pdf.addPiece(Range.between(2.0D, 3.0D), (x) -> 0.5D * square(3.0D - x));
                break;

            case 4:
                final double twothirds = 2.0D / 3.0D;
                final double twentytwoonthree = 22.0D / 3.0D;

                pdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x * x / 6.0D);
                pdf.addPiece(
                        Range.between(1.0D, 2.0D),
                        (x) -> {
                            double squared = x * x;

                            return -0.5D * x * squared + 2.0D * squared - 2.0D * x + twothirds;
                        });
                pdf.addPiece(
                        Range.between(2.0D, 3.0D),
                        (x) -> {
                            double squared = x * x;

                            return 0.5D * x * squared
                                    - 4.0D * squared
                                    + 10.0D * x
                                    - twentytwoonthree;
                        });
                pdf.addPiece(Range.between(3.0D, 4.0D), (x) -> cube(4.0D - x) / 6.0D);
                break;

            case 5:
                final double fivesixths = 5.0D / 6.0D;
                final double fiveontwentyfour = 5.0D / 24.0D;
                final double onefiftyfiveontwentyfour = 155.0D / 24.0D;
                final double sixfiftyfiveontwentyfour = 655.0D / 24.0D;

                pdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x * x * x / 24.0D);
                pdf.addPiece(
                        Range.between(1.0D, 2.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / -6.0D
                                    + fivesixths * cubed
                                    - 1.25D * squared
                                    + fivesixths * x
                                    - fiveontwentyfour;
                        });
                pdf.addPiece(
                        Range.between(2.0D, 3.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return 0.25D * x * cubed
                                    - 2.5D * cubed
                                    + 8.75D * squared
                                    - 12.5D * x
                                    + onefiftyfiveontwentyfour;
                        });
                pdf.addPiece(
                        Range.between(3.0D, 4.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / -6.0D
                                    + 2.5D * cubed
                                    - 13.75D * squared
                                    + 32.5D * x
                                    - sixfiftyfiveontwentyfour;
                        });
                pdf.addPiece(Range.between(4.0D, 5.0D), (x) -> fourth(5.0D - x) / 24.0D);
                break;
        }

        return pdf;
    }

    protected static PiecewiseFunction<Double, Double> buildCDF(int n) {
        PiecewiseFunction<Double, Double> cdf = new PiecewiseFunction<>();

        switch (n) {
            case 1:
                cdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x);
                break;

            case 2:
                cdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x / 2.0D);
                cdf.addPiece(Range.between(1.0D, 2.0D), (x) -> x * x / -2.0D + 2.0D * x - 1.0D);
                break;

            case 3:
                cdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x * x / 6.0D);
                cdf.addPiece(
                        Range.between(1.0D, 2.0D),
                        (x) -> {
                            double squared = x * x;

                            return x * squared / -3.0D + 1.5D * squared - 1.5D * x + 0.5D;
                        });
                cdf.addPiece(
                        Range.between(2.0D, 3.0D),
                        (x) -> {
                            double squared = x * x;

                            return x * squared / 6.0D - 1.5D * squared + 4.5D * x - 3.5D;
                        });
                break;

            case 4:
                final double twoonthree = 2.0D / 3.0D;
                final double sixth = 1.0D / 6.0D;
                final double fouronthree = 4.0D / 3.0D;
                final double twentytwoonthree = 22.0D / 3.0D;
                final double twentythreeonsix = 23.0D / 6.0D;
                final double thirtytwoonthree = 32.0D / 3.0D;
                final double twentynineonthree = 29.0D / 3.0D;

                cdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x * x * x / 24.0D);
                cdf.addPiece(
                        Range.between(1.0D, 2.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / -8.0D
                                    + twoonthree * cubed
                                    - squared
                                    + twoonthree * x
                                    - sixth;
                        });
                cdf.addPiece(
                        Range.between(2.0D, 3.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / 8.0D
                                    - fouronthree * cubed
                                    + 5 * squared
                                    - twentytwoonthree * x
                                    + twentythreeonsix;
                        });
                cdf.addPiece(
                        Range.between(3.0D, 4.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / -24.0D
                                    + twoonthree * cubed
                                    - 4 * squared
                                    + thirtytwoonthree * x
                                    - twentynineonthree;
                        });
                break;

            case 5:
                final double fiveontwentyfour = 5.0D / 24.0D;
                final double fiveontwelve = 5.0D / 12.0D;
                final double twentyfourth = 1.0D / 24.0D;
                final double thirtyfiveontwelve = 35.0D / 12.0D;
                final double onefiftyfiveontwentyfour = 155.0D / 24.0D;
                final double fiftyfiveontwelve = 55.0D / 12.0D;
                final double sixfiftyfiveontwentyfour = 655.0D / 24.0D;
                final double twentyfiveontwelve = 25.0D / 12.0D;
                final double onetwentyfiveontwelve = 125.0D / 12.0D;
                final double sixtwentyfiveontwentyfour = 625.0D / 24.0D;
                final double sixohoneontwentyfour = 601.0D / 24.0D;

                cdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x * x * x * x * x / 120.0D);
                cdf.addPiece(
                        Range.between(1.0D, 2.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;
                            double fourthed = cubed * x;

                            return x * fourthed / -30.0D
                                    + fiveontwentyfour * fourthed
                                    - fiveontwelve * cubed
                                    + fiveontwelve * squared
                                    - fiveontwentyfour * x
                                    + twentyfourth;
                        });
                cdf.addPiece(
                        Range.between(2.0D, 3.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;
                            double fourthed = cubed * x;

                            return 0.05D * x * fourthed
                                    - 0.625D * fourthed
                                    + thirtyfiveontwelve * cubed
                                    - 6.25D * squared
                                    + onefiftyfiveontwentyfour * x
                                    - 2.625D;
                        });
                cdf.addPiece(
                        Range.between(3.0D, 4.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;
                            double fourthed = cubed * x;

                            return x * fourthed / -30.0D
                                    + 0.625D * fourthed
                                    - fiftyfiveontwelve * cubed
                                    + 16.25D * squared
                                    - sixfiftyfiveontwentyfour * x
                                    + 17.625D;
                        });
                cdf.addPiece(
                        Range.between(4.0D, 5.0D),
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;
                            double fourthed = cubed * x;

                            return x * fourthed / 120.0D
                                    - fiveontwentyfour * fourthed
                                    + twentyfiveontwelve * cubed
                                    - onetwentyfiveontwelve * squared
                                    + sixtwentyfiveontwentyfour * x
                                    - sixohoneontwentyfour;
                        });
                break;
        }

        return cdf;
    }

    protected static PiecewiseFunction<Double, Double> buildICDF(int n) {
        PiecewiseFunction<Double, Double> icdf = new PiecewiseFunction<>();

        double twoonthree = 2.0D / 3.0D;

        switch (n) {
            case 1:
                icdf.addPiece(Range.between(0.0D, 1.0D), (x) -> x);
                break;

            case 2:
                icdf.addPiece(Range.between(0.0D, 0.5D), (x) -> Math.sqrt(2.0D * x));
                icdf.addPiece(
                        Range.between(0.5D, 1.0D), (x) -> -Math.sqrt(-2.0D * x + 2.0D) + 2.0D);
                break;

            case 3:
                double sqrt3 = Math.sqrt(3);
                double cbrt6 = Math.cbrt(6);

                Function<Double, Double> m1_3 =
                        (x) -> {
                            double squared = x * x;
                            return x * squared / -3.0D + 1.5D * squared - 1.5D * x + 0.5D;
                        };

                double s1_3 = m1_3.apply(1.0D);
                double s2_3 = m1_3.apply(2.0D);

                icdf.addPiece(Range.between(0.0D, s1_3), (x) -> Math.cbrt(6.0D * x));
                icdf.addPiece(
                        Range.between(s1_3, s2_3),
                        (x) ->
                                sqrt3
                                                * Mth.sin(
                                                        (float)
                                                                (Math.asin(
                                                                                twoonthree
                                                                                        * sqrt3
                                                                                        * (2.0D * x
                                                                                                - 1.0D))
                                                                        / 3.0D))
                                        + 1.5D);
                icdf.addPiece(Range.between(s2_3, 1.0D), (x) -> cbrt6 * Math.cbrt(x - 1.0D) + 3.0D);
                break;

            case 4:
                double twopowthreeonfour = Math.pow(2, 3.0F / 4.0F);
                double thirtytwoonthree = 32.0D / 3.0D;
                double twentynineonthree = 29.0D / 3.0D;

                Function<Double, Double> m1_4 = (x) -> x * x * x * x / 24.0D;

                Function<Double, Double> m2_4 =
                        (x) -> {
                            double squared = x * x;
                            double cubed = squared * x;

                            return x * cubed / -24.0D
                                    + twoonthree * cubed
                                    - 4 * squared
                                    + thirtytwoonthree * x
                                    - twentynineonthree;
                        };

                double s1_4 = m1_4.apply(1.0D);
                double s3_4 = m2_4.apply(3.0D);

                icdf.addPiece(
                        Range.between(0.0D, s1_4),
                        (x) -> twopowthreeonfour * Math.pow(3 * x, 0.25F));
                icdf.addPiece(
                        Range.between(s3_4, 1.0D),
                        (x) -> 4.0D - twopowthreeonfour * (Math.pow(3 - 3 * x, 0.25F)));
                break;

            case 5:
                double twopowthreeonfive = Math.pow(2, 3.0F / 5.0F);

                Function<Double, Double> m1_5 = (x) -> x * x * x * x * x / 120.0D;
                double s1_5 = m1_5.apply(1.0D);

                icdf.addPiece(
                        Range.between(0.0D, s1_5),
                        (x) -> twopowthreeonfive * Math.pow(15 * x, 0.2F));
                break;
        }

        return icdf;
    }

    private double transformToStandardSpace(double x) {
        return (x - lb) / preSupport;
    }

    private double transformFromStandardSpace(double x) {
        return x * preSupport + lb;
    }

    public double scaleAndClamp(double x) {
        return Mth.clamp(x, preLB, preUB) * n;
    }

    @Override
    public double density(double x) {
        if (x < this.getSupportLowerBound() || x > this.getSupportUpperBound()) {
            return 0.0D;
        }

        x = transformToStandardSpace(x);

        if (n <= 5) {
            return super.uncheckedDensity(x) / preSupport;
        }

        double r = 0.0D;

        for (int k = 0; k <= Mth.floor(x); k++) {
            r += numCache[k] * Math.pow(x - k, n - 1);
        }

        return r / (P_F * preSupport);
    }

    @Override
    public double cumulativeProbability(double x) {
        if (x < this.getSupportLowerBound()) {
            return 0.0D;
        } else if (x > this.getSupportUpperBound()) {
            return 1.0D;
        }

        x = transformToStandardSpace(x);

        if (n <= 5) {
            return super.uncheckedCumulativeProbability(x);
        }

        double r = 0.0D;

        for (int k = 0; k <= Mth.floor(x); k++) {
            r += numCache[k] * Math.pow(x - k, n);
        }

        return Mth.clamp(r / C_F, 0.0D, 1.0D);
    }

    @Override
    public double getNumericalMean() {
        return (ub + lb) / 2.0D;
    }

    @Override
    public double getNumericalVariance() {
        return preSupport * preSupport * n / 12.0D;
    }

    public double getStandardDeviation() {
        return preSupport * Math.sqrt(n) * INV_SQRT_12;
    }

    @Override
    public double getSupportLowerBound() {
        return lb;
    }

    @Override
    public double getSupportUpperBound() {
        return ub;
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
