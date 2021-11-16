package me.logwet.logmod;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import me.logwet.logmod.tools.overlay.hud.PlayerAttribute;
import me.logwet.logmod.tools.spawner.SpawnerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LogModData {
    private static final Cache<Long, SpawnerInfo> spawnerInfoCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static final Cache<UUID, PlayerAttribute> playerAttributeCache =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();

    private static final AtomicBoolean spawnersEnabled = new AtomicBoolean(true);
    private static final AtomicBoolean spawnerAnalysisEnabled = new AtomicBoolean(true);

    private static final AtomicBoolean renderSpawnerEnabled = new AtomicBoolean(true);
    private static final AtomicBoolean hudEnabled = new AtomicBoolean(true);

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

    @Environment(EnvType.CLIENT)
    public static boolean isHudEnabled() {
        return hudEnabled.get();
    }

    public static void onServerInit(MinecraftServer ms) {
        setMS(ms);
        resetPlayerAttributes();
        resetSpawnerInfo();
        LogMod.log(Level.INFO, "Server object initialized!");
    }
}
