package me.logwet.marathon.util.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class SpawnerInfo {
    private final BlockPos blockPos;
    private final AABB boundingBox;
    private final int numTrials;
    private final double[] successProbabilities;
    private final double avg;
    private final double[] probabilities;
    private final double[][][] probMatrix;

    public SpawnerInfo(
            BlockPos blockPos,
            AABB boundingBox,
            int numTrials,
            double[] successProbabilities,
            double avg,
            double[] probabilities,
            double[][][] probMatrix) {
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
        this.numTrials = numTrials;
        this.successProbabilities = successProbabilities;
        this.avg = avg;
        this.probabilities = probabilities;
        this.probMatrix = probMatrix;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AABB getBoundingBox() {
        return boundingBox;
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

    public double[][][] getProbMatrix() {
        return probMatrix;
    }
}
