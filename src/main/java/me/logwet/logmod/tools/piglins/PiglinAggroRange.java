package me.logwet.logmod.tools.piglins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PiglinAggroRange {
    @Nullable private final UUID entityTarget;
    @Nullable private final BlockPos blockTarget;
    @Nullable private final AABB range;
    private final List<UUID> entities;

    public PiglinAggroRange(@NotNull UUID entityTarget, @Nullable AABB range, List<UUID> entities) {
        this.entityTarget = entityTarget;
        this.blockTarget = null;
        this.range = range;
        this.entities = entities;
    }

    public PiglinAggroRange(
            @NotNull BlockPos blockTarget, @Nullable AABB range, List<UUID> entities) {
        this.entityTarget = null;
        this.blockTarget = blockTarget;
        this.range = range;
        this.entities = entities;
    }

    @Nullable
    public UUID getEntityTarget() {
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

    public List<UUID> getEntities() {
        return entities;
    }
}
