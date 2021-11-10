package me.logwet.marathon.util.spawner;

import me.logwet.marathon.util.spawner.distributions.PoissonBinomialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class RodStatistics {
    private final boolean enabled;

    private PoissonBinomialDistribution PBD;

    private double avgRodsPerCycle;
    private double avgCyclesForSixRods;
    private double avgTimeToSixRods;
    private double avgRodsPerMin;
    private double chanceOfSixRodsInMin;

    public RodStatistics() {
        this.enabled = false;
    }

    public RodStatistics(PoissonBinomialDistribution PBD, double lbCycleTime, double ubCycleTime) {
        this.enabled = true;
        this.PBD = PBD;

        UniformRealDistribution cycleTime = new UniformRealDistribution(lbCycleTime, ubCycleTime);

        double avgBlazesPerCycle = this.PBD.getMean();

        double avgRodsPerBlaze = 0.5D;

        double avgBlazesForSixRods = 6.0D / avgRodsPerBlaze;

        this.avgRodsPerCycle = avgBlazesPerCycle * avgRodsPerBlaze;

        this.avgCyclesForSixRods = 6.0D / this.avgRodsPerCycle;

        this.avgTimeToSixRods = this.avgCyclesForSixRods * cycleTime.getNumericalMean();

        this.avgRodsPerMin = 60.0D * this.avgRodsPerCycle / cycleTime.getNumericalMean();

        double chanceOfSixRodsInCycle = 0.0D;

        this.chanceOfSixRodsInMin = 0.0D;

        //        int numCycles = Mth.floor(60.0D / lbCycleTime);
        //
        //        IrwinHallDistribution cycleTimeDistribution;
        //
        //        for (int n = 1; n <= numCycles; n++) {
        //            cycleTimeDistribution = new IrwinHallDistribution(n, lbCycleTime,
        // ubCycleTime);
        //
        //            cycleTimeDistribution.cumulativeProbability(60.0D / n);
        //        }
    }

    private double irwinHallCDF(double x, int n) {
        double r = 0.0D;

        for (int k = 0; k <= Math.abs(x); k++) {
            r +=
                    Math.pow(-1, k)
                            * CombinatoricsUtils.binomialCoefficient(n, k)
                            * Math.pow(x - k, n);
        }

        r /= CombinatoricsUtils.factorial(n);

        return r;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getAvgRodsPerCycle() {
        return avgRodsPerCycle;
    }

    public double getAvgCyclesForSixRods() {
        return avgCyclesForSixRods;
    }

    public double getAvgTimeToSixRods() {
        return avgTimeToSixRods;
    }

    public double getAvgRodsPerMin() {
        return avgRodsPerMin;
    }

    public double getChanceOfSixRodsInMin() {
        return chanceOfSixRodsInMin;
    }
}
