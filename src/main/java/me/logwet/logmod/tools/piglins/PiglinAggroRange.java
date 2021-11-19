package me.logwet.logmod.tools.piglins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PiglinAggroRange {
    @Nullable private final Integer entityTarget;
    @Nullable private final BlockPos blockTarget;
    @Nullable private final AABB range;
    private final List<Integer> entities;

    public PiglinAggroRange(@NotNull Integer entityTarget, List<Integer> entities) {
        this.entityTarget = entityTarget;
        this.blockTarget = null;
        this.range = null;
        this.entities = entities;
    }

    public PiglinAggroRange(
            @NotNull BlockPos blockTarget, @NotNull AABB range, List<Integer> entities) {
        this.entityTarget = null;
        this.blockTarget = blockTarget;
        this.range = range;
        this.entities = entities;
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

    public List<Integer> getEntities() {
        return entities;
    }
}
