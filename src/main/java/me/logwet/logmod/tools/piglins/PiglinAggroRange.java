package me.logwet.logmod.tools.piglins;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PiglinAggroRange {
    @Nullable private final Integer entityTarget;
    @Nullable private final BlockPos blockTarget;
    @Nullable private final AABB range;

    public PiglinAggroRange(@NotNull Integer entityTarget) {
        this.entityTarget = entityTarget;
        this.blockTarget = null;
        this.range = null;
    }

    public PiglinAggroRange(
            @NotNull BlockPos blockTarget, @NotNull AABB range) {
        this.entityTarget = null;
        this.blockTarget = blockTarget;
        this.range = range;
    }

    @Nullable
    public Integer getEntityTarget() {
        return entityTarget;
    }

    @Nullable
    public BlockPos getBlockTarget() {
        return blockTarget;
    }

    @Nullable
    public AABB getRange() {
        return range;
    }
}
