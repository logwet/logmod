package me.logwet.marathon.util.spawner;

import me.logwet.marathon.util.spawner.distributions.EnumeratedIntegerDistributionImpl;
import me.logwet.marathon.util.spawner.distributions.IrwinHallDistribution;
import me.logwet.marathon.util.spawner.distributions.PoissonBinomialDistribution;
import net.minecraft.util.Mth;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
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

    public RodStatistics(PoissonBinomialDistribution PBD, double lbCycleTime, double ubCycleTime) {
        this.enabled = true;
        this.PBD = PBD;

        final double targetRods = 6.0D;

        UniformRealDistribution cycleTime = new UniformRealDistribution(lbCycleTime, ubCycleTime);

        double avgBlazesPerCycle = this.PBD.getMean();

        UniformIntegerDistribution rodDistribution = new UniformIntegerDistribution(0, 1);

        double avgRodsPerBlaze = rodDistribution.getNumericalMean();

        double avgBlazesForSixRods = targetRods / avgRodsPerBlaze;

        this.avgRodsPerCycle = avgBlazesPerCycle * avgRodsPerBlaze;

        this.avgCyclesForSixRods = targetRods / this.avgRodsPerCycle;

        this.avgTimeToSixRods = this.avgCyclesForSixRods * cycleTime.getNumericalMean();

        this.avgRodsPerMin = 60.0D * this.avgRodsPerCycle / cycleTime.getNumericalMean();

        int numCyclesToAnalyse = Mth.floor(60.0D / lbCycleTime);

        IrwinHallDistribution IHD;

        double prob;
        double probSum = 0.0D;
        double[] successProbabilities = new double[numCyclesToAnalyse + 1];
        successProbabilities[0] = 0.0D;

        int n;

        for (n = 1; n <= numCyclesToAnalyse; n++) {
            IHD = new IrwinHallDistribution(n, lbCycleTime, ubCycleTime);

            prob = IHD.cumulativeProbability(60.0D);

            if (prob == 0.0D) {
                n -= 1;
                break;
            }

            probSum += prob;
            successProbabilities[n] = prob;
        }

        for (int i = 0; i < numCyclesToAnalyse; i++) {
            successProbabilities[i] /= probSum;
        }

        EnumeratedIntegerDistributionImpl numCycleDistribution =
                new EnumeratedIntegerDistributionImpl(n, successProbabilities);

        int maxBlazesPerCycle = this.PBD.getSupportUpperBound();
        double maxRodsPerBlaze = rodDistribution.getSupportUpperBound();
        double maxRodsPerCycle = maxBlazesPerCycle * maxRodsPerBlaze;
        double minCycles = targetRods / maxRodsPerCycle;

        this.chanceOfSixRodsInMin =
                1.0D - numCycleDistribution.cumulativeProbability(Mth.floor(minCycles));
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
