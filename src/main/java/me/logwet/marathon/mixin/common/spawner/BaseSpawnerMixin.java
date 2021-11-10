package me.logwet.marathon.mixin.common.spawner;

import me.logwet.marathon.Marathon;
import me.logwet.marathon.MarathonData;
import me.logwet.marathon.util.spawner.BaseSpawnerAccessor;
import me.logwet.marathon.util.spawner.RodStatistics;
import me.logwet.marathon.util.spawner.SpawnerInfo;
import me.logwet.marathon.util.spawner.distributions.PoissonBinomialDistribution;
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
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;

import static me.logwet.marathon.Marathon.roundToString;
import static org.apache.logging.log4j.Level.INFO;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin implements BaseSpawnerAccessor {
    @Shadow private SpawnData nextSpawnData;
    @Shadow private int spawnRange;
    @Shadow private int requiredPlayerRange;
    @Shadow private int spawnDelay;
    @Unique private boolean finished = true;
    @Shadow private int spawnCount;
    @Shadow private int maxNearbyEntities;
    @Shadow private int maxSpawnDelay;
    @Shadow private int minSpawnDelay;

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    protected abstract void delay();

    @Unique
    private double bivariateTriangleDistribution(
            TriangularDistribution triangularDistribution,
            double x,
            double z,
            double invResolution) {
        double factor = invResolution / 2.0D;
        double lb = triangularDistribution.getSupportLowerBound();
        double ub = triangularDistribution.getSupportUpperBound();

        return triangularDistribution.probability(
                        Math.max(x - factor, lb), Math.min(x + factor, ub))
                * triangularDistribution.probability(
                        Math.max(z - factor, lb), Math.min(z + factor, ub));
    }

    @Unique
    private Component createMessageComponent(String message, ChatFormatting style) {
        return new TextComponent(message).withStyle(style);
    }

    @Unique
    private String createMessageString(
            EntityType<?> entityType, BlockPos blockPos, String message) {
        return StringUtils.capitalize(Registry.ENTITY_TYPE.getKey(entityType).getPath())
                + " spawner at "
                + blockPos.toShortString()
                + ", statistics: "
                + message;
    }

    @Unique
    private double[][][] cloneMatrix(double[][][] matrix) {
        return Arrays.stream(matrix)
                .map(m -> Arrays.stream(m).map(double[]::clone).toArray(double[][]::new))
                .toArray(double[][][]::new);
    }

    @Unique
    private AABB getNeighboursAABB(
            int x, int y, int z, int matrixWidth, int matrixHeight, int hShift, int vShift) {
        return new AABB(
                Math.max(x - hShift, 0),
                Math.max(y - vShift, 0),
                Math.max(z - hShift, 0),
                Math.min(x + hShift, matrixWidth - 1),
                Math.min(y + vShift, matrixHeight - 1),
                Math.min(z + hShift, matrixWidth - 1));
    }

    @Unique
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

    public long analyse() {
        long startTime = System.currentTimeMillis();

        Level level = this.getLevel();
        level.getProfiler().push("Analysis");

        BlockPos blockPos = this.getPos();

        CompoundTag spawnerTag = this.nextSpawnData.getTag();

        Optional<EntityType<?>> entityOptional = EntityType.by(spawnerTag);
        if (!entityOptional.isPresent()) {
            return -1;
        }
        EntityType<?> entityType = entityOptional.get();

        Entity entity = EntityType.loadEntityRecursive(spawnerTag, level, (entityx) -> entityx);
        if (entity == null) {
            return -1;
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
        double invResolution = 1D / (double) resolution;
        int hBound = this.spawnRange * resolution;
        int vBound = 3;

        TriangularDistribution triangularDistribution =
                new TriangularDistribution(-this.spawnRange, 0, this.spawnRange);

        Vec3 bbScaleFactor = new Vec3(this.spawnRange, (double) vBound / 2D, this.spawnRange);
        AABB boundingBox =
                new AABB(bbScaleFactor.scale(-1.0D), bbScaleFactor).move(0.5D, 0.5D, 0.5D);

        int matrixWidth = hBound * 2 + 1;
        int matrixHeight = vBound;

        double[][][] probMatrix = new double[matrixWidth][matrixHeight][matrixWidth];

        double matrixSum = 0;
        double matrixMaxSum = 0;

        AABB entityBoundingBox = entity.getBoundingBox();
        int hNShift =
                Mth.clamp(Math.round(entity.getBbWidth() * (float) resolution), 0, matrixWidth);
        int vNShift = Mth.clamp(Mth.ceil(entity.getBbHeight()) - 1, 0, matrixHeight);

        for (int x0 = -hBound; x0 <= hBound; x0++) {
            double x1 = ((double) x0 / (double) hBound) * (double) this.spawnRange;
            double x = (double) blockPos.getX() + x1 + 0.5D;

            for (int z0 = -hBound; z0 <= hBound; z0++) {
                double z1 = ((double) z0 / (double) hBound) * (double) this.spawnRange;
                double z = (double) blockPos.getZ() + z1 + 0.5D;

                double prob =
                        bivariateTriangleDistribution(
                                triangularDistribution, x1, z1, invResolution);

                matrixMaxSum += prob;

                for (int y0 = 0; y0 < vBound; y0++) {
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
                                probMatrix[x0 + hBound][y0][z0 + hBound] = prob;
                            }
                        } else {
                            matrixSum += prob;
                            probMatrix[x0 + hBound][y0][z0 + hBound] = prob;
                        }
                    }
                }
            }
        }
        matrixMaxSum *= vBound;

        double[] successProbabilities = new double[numTrials];

        double[][][] tempProbMatrix;

        /**
         * The following for loop contains the algorithm that forecasts for the impact of the
         * hitboxes of prospective blaze spawns. It was designed by Sharpieman20.
         */
        for (int i = 0; i < numTrials; i++) {
            if (i > 0) {
                tempProbMatrix = cloneMatrix(probMatrix);

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
                                                hNShift,
                                                vNShift);

                                double changed =
                                        tempProbMatrix[mx][my][mz] *=
                                                (1
                                                        - getBlockedProbFromNeighbours(
                                                                probMatrix, neighbours, matrixSum));

                                matrixSum += (changed - original);
                            }
                        }
                    }
                }

                probMatrix = tempProbMatrix;
            }

            successProbabilities[i] = matrixSum / matrixMaxSum;
        }

        entity.remove();
        tempProbMatrix = null;

        PoissonBinomialDistribution PBD =
                new PoissonBinomialDistribution(numTrials, successProbabilities);

        StringBuilder messageSuffix = new StringBuilder();

        messageSuffix.append("Avg: ").append(roundToString(PBD.getMean())).append(" Prob: ");

        for (int i = 0; i <= Mth.clamp(numTrials + 1, 0, this.spawnCount); i++) {
            messageSuffix
                    .append(i)
                    .append(": ")
                    .append(String.format("%.2f", PBD.getProbability(i) * 100D))
                    .append("% ");
        }

        String messageString = createMessageString(entityType, blockPos, messageSuffix.toString());

        Marathon.log(INFO, messageString);

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

        if (player != null) {
            if (player.isAlive()) {
                player.sendMessage(
                        createMessageComponent(messageString, ChatFormatting.GREEN), Util.NIL_UUID);
            }
        }

        RodStatistics rodStatistics =
                entityType == EntityType.BLAZE
                        ? new RodStatistics(
                                PBD,
                                (double) this.minSpawnDelay / 20.0D,
                                (double) this.maxSpawnDelay / 20.0D)
                        : new RodStatistics();

        MarathonData.addSpawnerInfo(
                blockPos,
                new SpawnerInfo(
                        blockPos,
                        boundingBox,
                        entityBoundingBox,
                        PBD,
                        probMatrix,
                        bivariateTriangleDistribution(triangularDistribution, 0, 0, invResolution),
                        rodStatistics));

        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;

        level.getProfiler().pop();

        Marathon.log(
                INFO,
                "Success Probabilities: "
                        + Arrays.toString(successProbabilities)
                        + " in "
                        + runTime
                        + "ms");

        return runTime;
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
                            shift = At.Shift.AFTER),
            cancellable = true)
    private void onSpawnAttemptStart(CallbackInfo ci) {
        if (MarathonData.isSpawnerAnalysisEnabled() && !this.finished) {
            analyse();
            this.finished = true;
        }

        if (!MarathonData.isSpawnersEnabled()) {
            this.delay();
            ci.cancel();
        }
    }
}
