package me.logwet.marathon.tools.spawner;

import me.logwet.marathon.Marathon;
import me.logwet.marathon.MarathonData;
import me.logwet.marathon.statistics.distributions.*;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.logging.log4j.Level;

public class RodStatistics {
    private final boolean enabled;

    private PoissonBinomialDistribution PBD;

    private double avgBlazesPerCycle;
    private double avgRodsPerCycle;
    private double avgCyclesForTargetRods;
    private double avgTimeToTargetRods;
    private double avgRodsPerTargetTime;
    private double chanceOfTargetRodsInTargetTime;

    public RodStatistics() {
        this.enabled = false;
    }

    public RodStatistics(PoissonBinomialDistribution PBD, double lbCycleTime, double ubCycleTime) {
        Marathon.log(Level.INFO, "Calculating rod statistics...");

        this.enabled = true;
        this.PBD = PBD;

        final int targetRods = MarathonData.getTargetRods();
        final double targetTime = MarathonData.getTargetTime();
        final int lootingLevel = MarathonData.getLootingLevel();

        ConvertedDiscreteDistribution<TrapezoidalDistribution> rodDistribution;

        if (lootingLevel == 0) {
            rodDistribution = DiscreteDistribution.from(new TrapezoidalDistribution(0, 0, 1, 1));
        } else {
            rodDistribution =
                    DiscreteDistribution.from(
                            new TrapezoidalDistribution(0, 1, lootingLevel, lootingLevel + 1));
        }

        ProductOfTwoDiscreteDistributions rodPerCycleDistribution =
                new ProductOfTwoDiscreteDistributions(PBD, rodDistribution);

        UniformRealDistribution cycleTimeDistribution =
                new UniformRealDistribution(lbCycleTime, ubCycleTime);

        ConvertedDiscreteDistribution<InverseUniformDistribution> cycleNumDistribution =
                DiscreteDistribution.from(
                        new InverseUniformDistribution(
                                cycleTimeDistribution.getSupportLowerBound(),
                                cycleTimeDistribution.getSupportUpperBound(),
                                targetTime));

        ProductOfTwoDiscreteDistributions targetRodsDistribution =
                new ProductOfTwoDiscreteDistributions(
                        rodPerCycleDistribution, cycleNumDistribution);

        this.avgBlazesPerCycle = this.PBD.getNumericalMean();

        double avgRodsPerBlaze = rodDistribution.getNumericalMean();

        this.avgRodsPerCycle = rodPerCycleDistribution.getNumericalMean();

        this.avgCyclesForTargetRods = (double) targetRods / this.avgRodsPerCycle;

        this.avgTimeToTargetRods =
                this.avgCyclesForTargetRods * cycleTimeDistribution.getNumericalMean();

        this.avgRodsPerTargetTime =
                targetTime * this.avgRodsPerCycle / cycleTimeDistribution.getNumericalMean();

        this.chanceOfTargetRodsInTargetTime =
                1.0D - targetRodsDistribution.cumulativeProbability(targetRods);

        Marathon.log(Level.INFO, "Rod statistics calculated.");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getAvgBlazesPerCycle() {
        return avgBlazesPerCycle;
    }

    public double getAvgRodsPerCycle() {
        return avgRodsPerCycle;
    }

    public double getAvgCyclesForTargetRods() {
        return avgCyclesForTargetRods;
    }

    public double getAvgTimeToTargetRods() {
        return avgTimeToTargetRods;
    }

    public double getAvgRodsPerTargetTime() {
        return avgRodsPerTargetTime;
    }

    public double getChanceOfTargetRodsInTargetTime() {
        return chanceOfTargetRodsInTargetTime;
    }
}
