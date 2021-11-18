package me.logwet.logmod.tools.trajectories;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Trajectory {
    private final List<Vec3> trajectory;
    private final BlockHitResult blockHitResult;

    public Trajectory(List<Vec3> trajectory, @Nullable BlockHitResult blockHitResult) {
        this.trajectory = trajectory;
        this.blockHitResult = blockHitResult;
    }

    public List<Vec3> getTrajectory() {
        return trajectory;
    }

    public BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }
}
