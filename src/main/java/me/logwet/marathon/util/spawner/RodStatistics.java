package me.logwet.marathon.util.spawner;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class RodStatistics {
    private final boolean enabled;
    private double avgRodsPerCycle;
    private double avgCyclesForSixRods;
    private double avgTimeToSixRods;
    private double avgRodsPerMin;
    private double chanceOfSixRodsInMin;

    public RodStatistics() {
        this.enabled = false;
    }

    public RodStatistics(
            double avgBlazesPerCycle,
            double lbCycleTime,
            double ubCycleTime,
            double[] cumulativeProbabilities) {
        this.enabled = true;

        UniformRealDistribution cycleTimeDistribution =
                new UniformRealDistribution(lbCycleTime, ubCycleTime);

        this.avgRodsPerCycle = avgBlazesPerCycle * 0.5D;

        this.avgCyclesForSixRods = 6.0D / this.avgRodsPerCycle;

        this.avgTimeToSixRods = this.avgCyclesForSixRods * cycleTimeDistribution.getNumericalMean();

        this.avgRodsPerMin =
                60.0D * this.avgRodsPerCycle / cycleTimeDistribution.getNumericalMean();

        this.chanceOfSixRodsInMin = 0.0D;
    }

    private static double round(double value) {
        return Math.round(value * 100.0D) / 100.0D;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getAvgRodsPerCycle() {
        return round(avgRodsPerCycle);
    }

    public double getAvgCyclesForSixRods() {
        return round(avgCyclesForSixRods);
    }

    public double getAvgTimeToSixRods() {
        return round(avgTimeToSixRods);
    }

    public double getAvgRodsPerMin() {
        return avgRodsPerMin;
    }

    public double getChanceOfSixRodsInMin() {
        return round(chanceOfSixRodsInMin);
    }
}
