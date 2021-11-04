package me.logwet.marathon.mixin.common.spawner;

import me.logwet.marathon.Marathon;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {
    @Shadow private SpawnData nextSpawnData;
    @Shadow private int spawnRange;
    @Shadow private int requiredPlayerRange;
    @Shadow private int spawnDelay;
    @Unique private boolean finished = true;
    @Shadow private int spawnCount;

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract BlockPos getPos();

    @Unique
    private double triangleDistribution(double pos, double range) {
        return (range - Math.abs(-pos)) / (range * range);
    }

    @Unique
    private double bivariateTriangleDistribution(double x, double z, double range) {
        return triangleDistribution(x, range) * triangleDistribution(z, range);
    }

    private Component createMessage(
            EntityType<?> entityType, BlockPos blockPos, String message, ChatFormatting style) {
        return new TextComponent(
                        StringUtils.capitalize(Registry.ENTITY_TYPE.getKey(entityType).getPath())
                                + " spawner at "
                                + blockPos.toShortString()
                                + ", statistics: "
                                + message)
                .withStyle(style);
    }

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/level/BaseSpawner;spawnDelay:I",
                            opcode = Opcodes.PUTFIELD,
                            shift = At.Shift.AFTER))
    private void onDeincrementSpawnDelay(CallbackInfo ci) {
        if (!this.getLevel().isClientSide) {
            if (this.spawnDelay == 0) {
                this.finished = false;
            }
        }
    }

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/nbt/CompoundTag;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/ListTag;"))
    private void onSpawnAttemptStart(CallbackInfo ci) {
        if (this.finished) {
            return;
        }

        Level level = this.getLevel();
        BlockPos blockPos = this.getPos();

        CompoundTag spawnerTag = this.nextSpawnData.getTag();

        Optional<EntityType<?>> entityOptional = EntityType.by(spawnerTag);
        if (!entityOptional.isPresent()) {
            return;
        }
        EntityType<?> entityType = entityOptional.get();

        Entity entity = EntityType.loadEntityRecursive(spawnerTag, level, (entityx) -> entityx);
        if (entity == null) {
            return;
        }

        int bound = this.spawnRange * 16;

        final int ySpawnRange = 3;

        double blockSuccess = 0;
        double entitySuccess = 0;
        double maxSuccess = 0;

        for (int x0 = -bound; x0 <= bound; x0++) {
            double x1 = ((double) x0 / (double) bound) * (double) this.spawnRange;
            double x = (double) blockPos.getX() + x1 + 0.5D;

            for (int z0 = -bound; z0 <= bound; z0++) {
                double z1 = ((double) z0 / (double) bound) * (double) this.spawnRange;
                double z = (double) blockPos.getZ() + z1 + 0.5D;

                double prob = bivariateTriangleDistribution(x1, z1, this.spawnRange);

                maxSuccess += prob;

                for (int y0 = 0; y0 < ySpawnRange; y0++) {
                    double y = (double) blockPos.getY() + y0 - 1;

                    if (level.noCollision(entityType.getAABB(x, y, z))
                            && SpawnPlacements.checkSpawnRules(
                                    entityType,
                                    level.getLevel(),
                                    MobSpawnType.SPAWNER,
                                    new BlockPos(x, y, z),
                                    level.getRandom())) {
                        if (entity instanceof Mob) {
                            Mob mob = (Mob) entity;
                            mob.moveTo(x, y, z, 0.0F, 0.0F);
                            if (mob.checkSpawnRules(level, MobSpawnType.SPAWNER)
                                    && mob.checkSpawnObstruction(level)) {
                                entitySuccess += prob;
                            }
                        }
                        blockSuccess += prob;
                    }
                }
            }
        }

        maxSuccess *= ySpawnRange;

        double blockProbability = blockSuccess / maxSuccess;
        double entityProbability = entitySuccess / maxSuccess;

        Marathon.log(
                org.apache.logging.log4j.Level.INFO,
                "Spawner: block/entity = "
                        + blockProbability * 100D
                        + " / "
                        + entityProbability * 100D);

        Player player =
                this.getLevel()
                        .getNearestPlayer(
                                (new TargetingConditions()).range(this.requiredPlayerRange),
                                blockPos.getX(),
                                blockPos.getY(),
                                blockPos.getZ());

        StringBuilder blockMessageString = new StringBuilder();
        StringBuilder entityMessageString = new StringBuilder();

        BinomialDistribution blockBinomial = new BinomialDistribution(4, blockProbability);
        BinomialDistribution entityBinomial = new BinomialDistribution(4, entityProbability);

        blockMessageString
                .append("Avg: ")
                .append(String.format("%.2f", blockBinomial.getNumericalMean()))
                .append(" Prob: ");

        entityMessageString
                .append("Avg: ")
                .append(String.format("%.2f", entityBinomial.getNumericalMean()))
                .append(" Prob: ");

        for (int i = 1; i <= this.spawnCount; i++) {
            blockMessageString
                    .append(i)
                    .append(": ")
                    .append(String.format("%.2f", blockBinomial.probability(i) * 100D))
                    .append("% ");
            entityMessageString
                    .append(i)
                    .append(": ")
                    .append(String.format("%.2f", entityBinomial.probability(i) * 100D))
                    .append("% ");
        }

        if (player != null) {
            if (player.isAlive()) {
                player.sendMessage(
                        createMessage(
                                entityType,
                                blockPos,
                                blockMessageString.toString(),
                                ChatFormatting.GREEN),
                        Util.NIL_UUID);
                player.sendMessage(
                        createMessage(
                                entityType,
                                blockPos,
                                entityMessageString.toString(),
                                ChatFormatting.GOLD),
                        Util.NIL_UUID);
            }
        }

        this.finished = true;
    }
}
