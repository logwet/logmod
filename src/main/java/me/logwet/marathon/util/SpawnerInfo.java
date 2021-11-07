package me.logwet.marathon.util;

public class SpawnerInfo {
    private final int numTrials;
    private final double[] successProbabilities;
    private final double avg;
    private final double[] probabilities;

    public SpawnerInfo(
            int numTrials, double[] successProbabilities, double avg, double[] probabilities) {
        this.numTrials = numTrials;
        this.successProbabilities = successProbabilities;
        this.avg = avg;
        this.probabilities = probabilities;
    }

    public int getNumTrials() {
        return numTrials;
    }

    public double[] getSuccessProbabilities() {
        return successProbabilities;
    }

    public double getAvg() {
        return avg;
    }

    public double[] getProbabilities() {
        return probabilities;
    }
}
