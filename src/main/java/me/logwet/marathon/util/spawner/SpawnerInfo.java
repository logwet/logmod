package me.logwet.marathon.util.spawner;

import me.logwet.marathon.util.spawner.distributions.PoissonBinomialDistribution;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class SpawnerInfo {
    private final BlockPos blockPos;
    private final AABB boundingBox;
    private final AABB entityBoundingBox;

    private final PoissonBinomialDistribution PBD;

    private final double[][][] probMatrix;
    private final double maxPossibleProb;

    private final RodStatistics rodStatistics;

    public SpawnerInfo(
            BlockPos blockPos,
            AABB boundingBox,
            AABB entityBoundingBox,
            PoissonBinomialDistribution PBD,
            double[][][] probMatrix,
            double maxPossibleProb,
            RodStatistics rodStatistics) {
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
        this.entityBoundingBox = entityBoundingBox;
        this.PBD = PBD;
        this.probMatrix = probMatrix;
        this.maxPossibleProb = maxPossibleProb;
        this.rodStatistics = rodStatistics;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public AABB getEntityBoundingBox() {
        return entityBoundingBox;
    }

    public PoissonBinomialDistribution getPBD() {
        return PBD;
    }

    public double[][][] getProbMatrix() {
        return probMatrix;
    }

    public double getMaxPossibleProb() {
        return maxPossibleProb;
    }

    public RodStatistics getRodStatistics() {
        return rodStatistics;
    }
}
