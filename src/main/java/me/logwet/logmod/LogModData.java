package me.logwet.logmod;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.logwet.logmod.tools.hud.PlayerAttribute;
import me.logwet.logmod.tools.paths.PathSet;
import me.logwet.logmod.tools.piglins.PiglinAggroRange;
import me.logwet.logmod.tools.spawner.SpawnerInfo;
import me.logwet.logmod.tools.trajectories.Trajectory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

public class LogModData {
    private static final Cache<Long, SpawnerInfo> spawnerInfoCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static final Cache<UUID, PlayerAttribute> playerAttributeCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static final Cache<UUID, Trajectory> trajectoryCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static final Cache<Long, PathSet> pathSetCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static final Cache<UUID, PiglinAggroRange> aggroRangeCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();

    private static final AtomicBoolean spawnersEnabled = new AtomicBoolean(true);
    private static final AtomicBoolean spawnerAnalysisEnabled = new AtomicBoolean(true);

    private static final AtomicBoolean renderSpawnerEnabled = new AtomicBoolean(true);
    private static final AtomicBoolean hudEnabled = new AtomicBoolean(true);

    private static final AtomicBoolean projectilesEnabled = new AtomicBoolean(true);
    private static final AtomicBoolean pathsEnabled = new AtomicBoolean(true);

    private static final AtomicBoolean piglinsEnabled = new AtomicBoolean(true);

    private static final AtomicInteger targetRods = new AtomicInteger(6);
    private static final AtomicDouble targetTime = new AtomicDouble(60.0D);
    private static final AtomicInteger lootingLevel = new AtomicInteger(0);

    private static MinecraftServer MS;

    public static MinecraftServer getMS() {
        return MS;
    }

    private static void setMS(MinecraftServer MS) {
        LogModData.MS = MS;
    }

    private static Cache<UUID, PlayerAttribute> getPlayerAttributeCache() {
        return playerAttributeCache;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static PlayerAttribute getPlayerAttribute(UUID uuid) {
        return getPlayerAttributeCache().getIfPresent(uuid);
    }

    public static void addPlayerAttribute(UUID uuid, PlayerAttribute playerAttribute) {
        getPlayerAttributeCache().put(uuid, playerAttribute);
    }

    public static void removePlayerAttribute(UUID uuid) {
        getPlayerAttributeCache().invalidate(uuid);
    }

    private static void resetPlayerAttributes() {
        getPlayerAttributeCache().invalidateAll();
        getPlayerAttributeCache().cleanUp();
    }

    private static Cache<Long, SpawnerInfo> getSpawnerInfoCache() {
        return spawnerInfoCache;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static SpawnerInfo getSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoCache().getIfPresent(blockPos.asLong());
    }

    public static void addSpawnerInfo(BlockPos blockPos, SpawnerInfo spawnerInfo) {
        getSpawnerInfoCache().put(blockPos.asLong(), spawnerInfo);
    }

    public static void removeSpawnerInfo(BlockPos blockPos) {
        getSpawnerInfoCache().invalidate(blockPos.asLong());
    }

    private static void resetSpawnerInfo() {
        getSpawnerInfoCache().invalidateAll();
        getSpawnerInfoCache().cleanUp();
    }

    private static Cache<UUID, Trajectory> getTrajectoryCache() {
        return trajectoryCache;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static Trajectory getTrajectory(UUID uuid) {
        return getTrajectoryCache().getIfPresent(uuid);
    }

    public static void addTrajectory(UUID uuid, Trajectory trajectory) {
        getTrajectoryCache().put(uuid, trajectory);
    }

    public static void removeTrajectory(UUID uuid) {
        getTrajectoryCache().invalidate(uuid);
    }

    private static void resetTrajectories() {
        getTrajectoryCache().invalidateAll();
        getTrajectoryCache().cleanUp();
    }

    private static Cache<Long, PathSet> getPathSetCache() {
        return pathSetCache;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static PathSet getPathSet(long seed) {
        return getPathSetCache().getIfPresent(seed);
    }

    public static void addPathSet(long seed, PathSet pathSet) {
        getPathSetCache().put(seed, pathSet);
    }

    public static void removePathSet(long seed) {
        getPathSetCache().invalidate(seed);
    }

    private static void resetPathCaches() {
        getPathSetCache().invalidateAll();
        getPathSetCache().cleanUp();
    }

    private static Cache<UUID, PiglinAggroRange> getAggroRangeCache() {
        return aggroRangeCache;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static PiglinAggroRange getAggroRange(UUID uuid) {
        return getAggroRangeCache().getIfPresent(uuid);
    }

    public static void addAggroRange(UUID uuid, PiglinAggroRange aggroRange) {
        getAggroRangeCache().put(uuid, aggroRange);
    }

    public static void removeAggroRange(UUID uuid) {
        getAggroRangeCache().invalidate(uuid);
    }

    private static void resetAggroRanges() {
        getAggroRangeCache().invalidateAll();
        getAggroRangeCache().cleanUp();
    }

    public static boolean toggleSpawnersEnabled() {
        return !spawnersEnabled.getAndSet(!spawnersEnabled.get());
    }

    public static boolean isSpawnersEnabled() {
        return spawnersEnabled.get();
    }

    public static boolean toggleSpawnerAnalysis() {
        return !spawnerAnalysisEnabled.getAndSet(!spawnerAnalysisEnabled.get());
    }

    public static boolean isSpawnerAnalysisEnabled() {
        return spawnerAnalysisEnabled.get();
    }

    public static boolean toggleRenderSpawnersEnabled() {
        return !renderSpawnerEnabled.getAndSet(!renderSpawnerEnabled.get());
    }

    public static boolean isRenderSpawnersEnabled() {
        return renderSpawnerEnabled.get();
    }

    public static boolean toggleHudEnabled() {
        return !hudEnabled.getAndSet(!hudEnabled.get());
    }

    public static boolean isHudEnabled() {
        return hudEnabled.get();
    }

    public static boolean toggleProjectilesEnabled() {
        return !projectilesEnabled.getAndSet(!projectilesEnabled.get());
    }

    public static boolean isProjectilesEnabled() {
        return projectilesEnabled.get();
    }

    public static boolean togglePathsEnabled() {
        return !pathsEnabled.getAndSet(!projectilesEnabled.get());
    }

    public static boolean isPathsEnabled() {
        return pathsEnabled.get();
    }

    public static boolean togglePiglinsEnabled() {
        return !piglinsEnabled.getAndSet(!piglinsEnabled.get());
    }

    public static boolean isPiglinsEnabled() {
        return piglinsEnabled.get();
    }

    public static int getTargetRods() {
        return targetRods.get();
    }

    public static void setTargetRods(int value) {
        targetRods.set(value);
    }

    public static double getTargetTime() {
        return targetTime.get();
    }

    public static void setTargetTime(double value) {
        targetTime.set(value);
    }

    public static int getLootingLevel() {
        return lootingLevel.get();
    }

    public static void setLootingLevel(int value) {
        lootingLevel.set(value);
    }

    public static void onServerInit(MinecraftServer ms) {
        setMS(ms);

        resetPlayerAttributes();
        resetSpawnerInfo();
        resetTrajectories();
        resetAggroRanges();

        LogMod.log(Level.INFO, "Server object initialized!");
    }
}
