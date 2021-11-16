package me.logwet.logmod.tools.spawner;

import me.logwet.logmod.Marathon;
import me.logwet.logmod.MarathonData;
import me.logwet.logmod.statistics.distributions.*;
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
        long startTime = System.currentTimeMillis();

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

        ProductOfDiscreteDistributions<
                        PoissonBinomialDistribution,
                        ConvertedDiscreteDistribution<TrapezoidalDistribution>>
                rodsPerCycleDistribution =
                        DiscreteDistribution.productOf(blazeNumDistribution, rodDistribution);

        UniformRealDistribution cycleTimeDistribution =
                new UniformRealDistribution(lbCycleTime, ubCycleTime);

        int minCyclesToAnalyse = Mth.floor(targetTime / ubCycleTime);
        int maxCyclesToAnalyse = Mth.floor(targetTime / lbCycleTime);
        int numCyclesToAnalyse = maxCyclesToAnalyse - minCyclesToAnalyse;
        double[] cycleNumSuccessProbs = new double[numCyclesToAnalyse + 1];

        ConvertedDiscreteDistribution<InverseUniformDistribution> cycleProportionDistribution =
                DiscreteDistribution.from(
                        new InverseUniformDistribution(
                                targetTime / maxCyclesToAnalyse,
                                targetTime / minCyclesToAnalyse,
                                targetTime));

        for (int n = minCyclesToAnalyse; n <= maxCyclesToAnalyse; n++) {
            IrwinHallDistribution summedCycleDistribution =
                    new IrwinHallDistribution(n, lbCycleTime, ubCycleTime);

            cycleNumSuccessProbs[n - minCyclesToAnalyse] =
                    summedCycleDistribution.cumulativeProbability(targetTime);
            //                    * cycleProportionDistribution.probability(n);
        }

        //        DiscreteDistribution adjustedCycleNumDistribution =
        //                new DiscreteDistribution(
        //                        minCyclesToAnalyse, numCyclesToAnalyse, cycleNumSuccessProbs);

        PoissonBinomialDistribution adjustedCycleNumDistribution =
                new PoissonBinomialDistribution(
                        minCyclesToAnalyse, numCyclesToAnalyse, cycleNumSuccessProbs);

        ProductOfDiscreteDistributions<
                        ProductOfDiscreteDistributions<
                                PoissonBinomialDistribution,
                                ConvertedDiscreteDistribution<TrapezoidalDistribution>>,
                        PoissonBinomialDistribution>
                targetRodsDistribution =
                        DiscreteDistribution.productOf(
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

        long endTime = System.currentTimeMillis();
        Marathon.log(Level.INFO, "Rod statistics calculated in " + (endTime - startTime) + "ms");
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
