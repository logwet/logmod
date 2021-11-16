package me.logwet.logmod.tools.overlay.trajectories;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Trajectory extends PlayerState {
    private final List<Vec3> trajectory;
    private final BlockHitResult blockHitResult;

    public Trajectory(
            Vec3 pos, Vec2 rot, List<Vec3> trajectory, @Nullable BlockHitResult blockHitResult) {
        super(pos, rot);

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
