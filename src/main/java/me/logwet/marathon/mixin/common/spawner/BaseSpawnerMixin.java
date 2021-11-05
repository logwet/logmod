package me.logwet.marathon.mixin.common.spawner;

import me.logwet.marathon.Marathon;
import me.logwet.marathon.util.VariableBinomialDistribution;
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
import java.util.stream.DoubleStream;

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

    private AABB getNeighboursAABB(int x, int y, int z, int matrixWidth, int matrixHeight) {
        return new AABB(
                Mth.clamp(x - 11, 0, matrixWidth - 1),
                Mth.clamp(y, 0, matrixHeight - 1),
                Mth.clamp(z - 11, 0, matrixWidth - 1),
                Mth.clamp(x + 11, 0, matrixWidth - 1),
                Mth.clamp(y + 1, 0, matrixHeight - 1),
                Mth.clamp(z + 11, 0, matrixWidth - 1));
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

    private DoubleStream flattenMatrixToStream(double[][][] matrix) {
        return Arrays.stream(matrix)
                .flatMapToDouble(x -> Arrays.stream(x).flatMapToDouble(Arrays::stream));
    }

    private double sumStream(DoubleStream stream) {
        return stream.reduce(Double::sum).getAsDouble();
    }

    private double sumMatrix(double[][][] matrix) {
        return sumStream(flattenMatrixToStream(matrix));
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

        int bound = this.spawnRange * 16;

        final int ySpawnRange = 3;

        int matrixWidth = bound * 2 + 1;
        int matrixHeight = ySpawnRange;

        double[][][] unblockedProbMatrix = new double[matrixWidth][matrixHeight][matrixWidth];

        double success = 0;
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
                                success += prob;
                                unblockedProbMatrix[x0 + bound][y0][z0 + bound] = prob;
                            }
                        }
                    }
                }
            }
        }
        maxSuccess *= ySpawnRange;

        double unblockedSum = success;

        double[] probabilities = new double[this.spawnCount + 1];
        probabilities[0] = unblockedSum / maxSuccess;

        for (int i = 0; i < this.spawnCount; i++) {
            if (i > 0) {
                for (int mx = 0; mx < matrixWidth; mx++) {
                    for (int my = 0; my < matrixHeight; my++) {
                        for (int mz = 0; mz < matrixWidth; mz++) {
                            double original;

                            if ((original = unblockedProbMatrix[mx][my][mz]) > 0D) {
                                AABB neighbours =
                                        getNeighboursAABB(mx, my, mz, matrixWidth, matrixHeight);

                                double changed =
                                        unblockedProbMatrix[mx][my][mz] *=
                                                (1
                                                        - getBlockedProbFromNeighbours(
                                                                unblockedProbMatrix,
                                                                neighbours,
                                                                unblockedSum));

                                unblockedSum += (changed - original);
                            }
                        }
                    }
                }
            }

            double prob = unblockedSum / maxSuccess;
            probabilities[i + 1] = prob;

            System.out.println(
                    "Spawn Attempt " + (i + 1) + ": " + String.format("%.2f", prob * 100D) + "%");
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

        StringBuilder messageString = new StringBuilder();

        VariableBinomialDistribution variableBinomial =
                new VariableBinomialDistribution(
                        Mth.clamp(
                                this.maxNearbyEntities - numEntitiesInVicinity,
                                0,
                                this.spawnCount));

        messageString
                .append("Avg: ")
                .append(String.format("%.2f", variableBinomial.getNumericalMean(probabilities)))
                .append(" Prob: ");

        for (int i = 0; i <= this.spawnCount; i++) {
            messageString
                    .append(i)
                    .append(": ")
                    .append(
                            String.format(
                                    "%.2f",
                                    variableBinomial.probability(i, probabilities[i]) * 100D))
                    .append("% ");
        }

        if (player != null) {
            if (player.isAlive()) {
                player.sendMessage(
                        createMessage(
                                entityType,
                                blockPos,
                                messageString.toString(),
                                ChatFormatting.GREEN),
                        Util.NIL_UUID);
            }
        }

        long endTime = System.currentTimeMillis();
        Marathon.log(
                INFO,
                "Spawner "
                        + blockPos.toShortString()
                        + ", Probabilities: "
                        + Arrays.toString(probabilities)
                        + " in "
                        + (endTime - startTime)
                        + "ms");

        this.finished = true;
    }
}
