package me.logwet.marathon.tools.spawner;

import me.logwet.marathon.statistics.distributions.EnumeratedIntegerDistributionImpl;
import me.logwet.marathon.statistics.distributions.IrwinHallDistribution;
import me.logwet.marathon.statistics.distributions.PoissonBinomialDistribution;
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
        final double targetTime = 60.0D;

        UniformRealDistribution cycleTime = new UniformRealDistribution(lbCycleTime, ubCycleTime);

        double avgBlazesPerCycle = this.PBD.getMean();

        UniformIntegerDistribution rodDistribution = new UniformIntegerDistribution(0, 1);

        double avgRodsPerBlaze = rodDistribution.getNumericalMean();

        double avgBlazesForSixRods = targetRods / avgRodsPerBlaze;

        this.avgRodsPerCycle = avgBlazesPerCycle * avgRodsPerBlaze;

        this.avgCyclesForSixRods = targetRods / this.avgRodsPerCycle;

        this.avgTimeToSixRods = this.avgCyclesForSixRods * cycleTime.getNumericalMean();

        this.avgRodsPerMin = targetTime * this.avgRodsPerCycle / cycleTime.getNumericalMean();

        int numCyclesToAnalyse = Mth.floor(targetTime / lbCycleTime);

        double[] probabilities = new double[numCyclesToAnalyse + 1];
        probabilities[0] = 0.0D;
        double probSum = 0.0D;

        int maxBlazesPerCycle = this.PBD.getSupportUpperBound();
        double maxRodsPerBlaze = rodDistribution.getSupportUpperBound();
        double maxRodsPerCycle = maxBlazesPerCycle * maxRodsPerBlaze;
        double minCyclesR = targetRods / maxRodsPerCycle;
        int minCycles = Mth.ceil(minCyclesR);

        int n;

        for (n = 1; n <= numCyclesToAnalyse; n++) {
            IrwinHallDistribution numCycleDistribution =
                    new IrwinHallDistribution(n, lbCycleTime, ubCycleTime);

            // probNCyclesUnderMin is probability that for n number of cycles, all of them occur in
            // under a minute
            double probNCyclesUnderMin = numCycleDistribution.cumulativeProbability(targetTime);

            if (probNCyclesUnderMin == 0.0D) {
                n -= 1;
                break;
            }

            probSum += probNCyclesUnderMin;
            probabilities[n] = probNCyclesUnderMin;
        }

        // cyclesDistribution is a distribution of the probability that a given number of cycles
        // occur in a minute.
        EnumeratedIntegerDistributionImpl cyclesDistribution =
                new EnumeratedIntegerDistributionImpl(n, probabilities);

        double chanceCyclesInValidRange =
                1.0D - cyclesDistribution.cumulativeProbability(Mth.floor(minCyclesR));

        this.chanceOfSixRodsInMin = 1.0D;
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
