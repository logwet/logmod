package me.logwet.marathon.mixin.common.spawner;

import me.logwet.marathon.Marathon;
import me.logwet.marathon.util.PoissonBinomialDistribution;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;

import static org.apache.logging.log4j.Level.INFO;

/** @author logwet & Sharpieman20 */
@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {
    @Shadow private SpawnData nextSpawnData;
    @Shadow private int spawnRange;
    @Shadow private int requiredPlayerRange;
    @Shadow private int spawnDelay;
    @Unique private boolean finished = true;
    @Shadow private int spawnCount;
    @Shadow private int maxNearbyEntities;

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

    private Component createMessageComponent(String message, ChatFormatting style) {
        return new TextComponent(message).withStyle(style);
    }

    private String createMessageString(
            EntityType<?> entityType, BlockPos blockPos, String message) {
        return StringUtils.capitalize(Registry.ENTITY_TYPE.getKey(entityType).getPath())
                + " spawner at "
                + blockPos.toShortString()
                + ", statistics: "
                + message;
    }

    private AABB getNeighboursAABB(
            int x,
            int y,
            int z,
            int matrixWidth,
            int matrixHeight,
            float entityWidth,
            int resolution) {
        int shift = Math.round(entityWidth * (float) resolution);
        return new AABB(
                Mth.clamp(x - shift, 0, matrixWidth - 1),
                Mth.clamp(y, 0, matrixHeight - 1),
                Mth.clamp(z - shift, 0, matrixWidth - 1),
                Mth.clamp(x + shift, 0, matrixWidth - 1),
                Mth.clamp(y + 1, 0, matrixHeight - 1),
                Mth.clamp(z + shift, 0, matrixWidth - 1));
    }

    private double getBlockedProbFromNeighbours(double[][][] matrix, AABB neighbours, double sum) {
        double r = 0D;
        for (int x = (int) neighbours.minX; x <= neighbours.maxX; x++) {
            for (int y = (int) neighbours.minY; y <= neighbours.maxY; y++) {
                for (int z = (int) neighbours.minZ; z <= neighbours.maxZ; z++) {
                    r += matrix[x][y][z] / sum;
                }
            }
        }
        return r;
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
                                    "Lnet/minecraft/nbt/CompoundTag;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/ListTag;",
                            shift = At.Shift.AFTER))
    private void onSpawnAttemptStart(CallbackInfo ci) {
        if (this.finished) {
            return;
        }

        long startTime = System.currentTimeMillis();

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

        int numEntitiesInVicinity =
                level.getEntitiesOfClass(
                                entity.getClass(),
                                (new AABB(
                                                blockPos.getX(),
                                                blockPos.getY(),
                                                blockPos.getZ(),
                                                blockPos.getX() + 1,
                                                blockPos.getY() + 1,
                                                blockPos.getZ() + 1))
                                        .inflate(this.spawnRange))
                        .size();

        int numTrials =
                Mth.clamp(this.maxNearbyEntities - numEntitiesInVicinity, 0, this.spawnCount);

        int resolution = 16;
        int bound = this.spawnRange * resolution;

        int ySpawnRange = 3;

        int matrixWidth = bound * 2 + 1;
        int matrixHeight = ySpawnRange;

        double[][][] probMatrix = new double[matrixWidth][matrixHeight][matrixWidth];

        double matrixSum = 0;
        double matrixMaxSum = 0;

        for (int x0 = -bound; x0 <= bound; x0++) {
            double x1 = ((double) x0 / (double) bound) * (double) this.spawnRange;
            double x = (double) blockPos.getX() + x1 + 0.5D;

            for (int z0 = -bound; z0 <= bound; z0++) {
                double z1 = ((double) z0 / (double) bound) * (double) this.spawnRange;
                double z = (double) blockPos.getZ() + z1 + 0.5D;

                double prob = bivariateTriangleDistribution(x1, z1, this.spawnRange);

                matrixMaxSum += prob;

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
                                matrixSum += prob;
                                probMatrix[x0 + bound][y0][z0 + bound] = prob;
                            }
                        } else {
                            matrixSum += prob;
                            probMatrix[x0 + bound][y0][z0 + bound] = prob;
                        }
                    }
                }
            }
        }
        matrixMaxSum *= ySpawnRange;

        double[] successProbabilities = new double[numTrials];

        for (int i = 0; i < numTrials; i++) {
            if (i > 0) {
                for (int mx = 0; mx < matrixWidth; mx++) {
                    for (int my = 0; my < matrixHeight; my++) {
                        for (int mz = 0; mz < matrixWidth; mz++) {
                            double original;
                            if ((original = probMatrix[mx][my][mz]) > 0D) {

                                AABB neighbours =
                                        getNeighboursAABB(
                                                mx,
                                                my,
                                                mz,
                                                matrixWidth,
                                                matrixHeight,
                                                entity.getBbWidth(),
                                                resolution);

                                double changed =
                                        probMatrix[mx][my][mz] *=
                                                (1
                                                        - getBlockedProbFromNeighbours(
                                                                probMatrix, neighbours, matrixSum));

                                matrixSum += (changed - original);
                            }
                        }
                    }
                }
            }

            successProbabilities[i] = matrixSum / matrixMaxSum;
        }

        entity.remove();

        Player player =
                this.getLevel()
                        .getNearestPlayer(
                                (new TargetingConditions())
                                        .range(this.requiredPlayerRange * 1.5D)
                                        .allowInvulnerable()
                                        .allowUnseeable()
                                        .ignoreInvisibilityTesting(),
                                blockPos.getX(),
                                blockPos.getY(),
                                blockPos.getZ());

        StringBuilder messageSuffix = new StringBuilder();

        PoissonBinomialDistribution PBD =
                new PoissonBinomialDistribution(numTrials, successProbabilities);

        messageSuffix
                .append("Avg: ")
                .append(String.format("%.2f", PBD.getNumericalMean()))
                .append(" Prob: ");

        for (int i = 0; i <= Mth.clamp(numTrials + 1, 0, this.spawnCount); i++) {
            messageSuffix
                    .append(i)
                    .append(": ")
                    .append(String.format("%.2f", PBD.getProbability(i) * 100D))
                    .append("% ");
        }

        String messageString = createMessageString(entityType, blockPos, messageSuffix.toString());

        Marathon.log(INFO, messageString);

        if (player != null) {
            if (player.isAlive()) {
                player.sendMessage(
                        createMessageComponent(messageString, ChatFormatting.GREEN), Util.NIL_UUID);
            }
        }

        long endTime = System.currentTimeMillis();
        Marathon.log(
                INFO,
                "Success Probabilities: "
                        + Arrays.toString(successProbabilities)
                        + " in "
                        + (endTime - startTime)
                        + "ms");

        this.finished = true;
    }
}
