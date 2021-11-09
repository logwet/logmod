package me.logwet.marathon.util.spawner;

import org.apache.commons.math3.distribution.UniformRealDistribution;

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

    public RodStatistics(
            PoissonBinomialDistribution poissonBinomialDistribution,
            double lbCycleTime,
            double ubCycleTime) {
        this.enabled = true;

        this.PBD = poissonBinomialDistribution;

        UniformRealDistribution cycleTimeDistribution =
                new UniformRealDistribution(lbCycleTime, ubCycleTime);

        double avgBlazesPerCycle = this.PBD.getMean();

        double avgRodsPerBlaze = 0.5D;

        double avgBlazesForSixRods = 6.0D / avgRodsPerBlaze;

        this.avgRodsPerCycle = avgBlazesPerCycle * avgRodsPerBlaze;

        this.avgCyclesForSixRods = 6.0D / this.avgRodsPerCycle;

        this.avgTimeToSixRods = this.avgCyclesForSixRods * cycleTimeDistribution.getNumericalMean();

        this.avgRodsPerMin =
                60.0D * this.avgRodsPerCycle / cycleTimeDistribution.getNumericalMean();

        this.chanceOfSixRodsInMin = 0.0D;
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
