package me.logwet.marathon.tools.spawner;

import me.logwet.marathon.Marathon;
import me.logwet.marathon.MarathonData;
import me.logwet.marathon.statistics.distributions.*;
import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.logging.log4j.Level;

public class RodStatistics {
    private final boolean enabled;

    private double avgBlazesPerCycle;
    private double avgRodsPerCycle;
    private double avgCyclesForTargetRods;
    private double avgTimeToTargetRods;
    private double avgRodsPerTargetTime;
    private double chanceOfTargetRodsInTargetTime;

    public RodStatistics() {
        this.enabled = false;
    }

    public RodStatistics(
            PoissonBinomialDistribution blazeNumDistribution,
            double lbCycleTime,
            double ubCycleTime) {
        Marathon.log(Level.INFO, "Calculating rod statistics...");

        this.enabled = true;

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

        ProductOfTwoDiscreteDistributions rodsPerCycleDistribution =
                new ProductOfTwoDiscreteDistributions(blazeNumDistribution, rodDistribution);

        UniformRealDistribution cycleTimeDistribution =
                new UniformRealDistribution(lbCycleTime, ubCycleTime);

        int minCyclesToAnalyse = Mth.floor(targetTime / ubCycleTime);
        int maxCyclesToAnalyse = Mth.floor(targetTime / lbCycleTime);
        int numCyclesToAnalyse = maxCyclesToAnalyse - minCyclesToAnalyse;
        double[] cycleNumProbs = new double[numCyclesToAnalyse + 1];

        ConvertedDiscreteDistribution<InverseUniformDistribution> cycleProportionDistribution =
                DiscreteDistribution.from(
                        new InverseUniformDistribution(
                                targetTime / maxCyclesToAnalyse,
                                targetTime / minCyclesToAnalyse,
                                targetTime));

        for (int n = minCyclesToAnalyse; n <= maxCyclesToAnalyse; n++) {
            IrwinHallDistribution summedCycleDistribution =
                    new IrwinHallDistribution(n, lbCycleTime, ubCycleTime);

            // For n number of cycles, the following is the probability that they occur within the
            // target time.
            cycleNumProbs[n - minCyclesToAnalyse] =
                    summedCycleDistribution.cumulativeProbability(targetTime);
        }

        PoissonBinomialDistribution cycleNumDistribution =
                new PoissonBinomialDistribution(
                        minCyclesToAnalyse, numCyclesToAnalyse, cycleNumProbs);

        ElementwiseProductDistribution<
                        PoissonBinomialDistribution,
                        ConvertedDiscreteDistribution<InverseUniformDistribution>>
                adjustedCycleNumDistribution =
                        DiscreteDistribution.elementwiseProductOf(
                                cycleNumDistribution, cycleProportionDistribution);

        ProductOfTwoDiscreteDistributions targetRodsDistribution =
                new ProductOfTwoDiscreteDistributions(
                        rodsPerCycleDistribution, adjustedCycleNumDistribution);

        this.avgBlazesPerCycle = blazeNumDistribution.getNumericalMean();

        this.avgRodsPerCycle = rodsPerCycleDistribution.getNumericalMean();

        this.avgCyclesForTargetRods = (double) targetRods / this.avgRodsPerCycle;

        this.avgTimeToTargetRods =
                this.avgCyclesForTargetRods * cycleTimeDistribution.getNumericalMean();

        this.avgRodsPerTargetTime =
                targetTime * this.avgRodsPerCycle / cycleTimeDistribution.getNumericalMean();

        this.chanceOfTargetRodsInTargetTime =
                1.0D - targetRodsDistribution.cumulativeProbability(targetRods - 1);

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
