package me.logwet.marathon;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.logwet.marathon.util.spawner.SpawnerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

public class MarathonData {
    private static final Cache<Long, SpawnerInfo> spawnerInfoMap =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static MinecraftServer MS;
    private static boolean spawnersEnabled = true;
    private static boolean spawnerAnalysisEnabled = true;

    public static MinecraftServer getMS() {
        return MS;
    }

    private static void setMS(MinecraftServer MS) {
        MarathonData.MS = MS;
    }

    private static Cache<Long, SpawnerInfo> getSpawnerInfoMap() {
        return spawnerInfoMap;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static SpawnerInfo getSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoMap().getIfPresent(blockPos.asLong());
    }

    public static void addSpawnerInfo(BlockPos blockPos, SpawnerInfo spawnerInfo) {
        getSpawnerInfoMap().put(blockPos.asLong(), spawnerInfo);
    }

    public static void removeSpawnerInfo(BlockPos blockPos) {
        getSpawnerInfoMap().invalidate(blockPos.asLong());
    }

    public static boolean toggleSpawnersEnabled() {
        spawnersEnabled = !spawnersEnabled;
        return spawnersEnabled;
    }

    public static boolean isSpawnersEnabled() {
        return spawnersEnabled;
    }

    public static boolean toggleSpawnerAnalysis() {
        spawnerAnalysisEnabled = !spawnerAnalysisEnabled;
        return spawnerAnalysisEnabled;
    }

    public static boolean isSpawnerAnalysisEnabled() {
        return spawnerAnalysisEnabled;
    }

    public static void onServerInit(MinecraftServer ms) {
        setMS(ms);
        getSpawnerInfoMap().invalidateAll();
        getSpawnerInfoMap().cleanUp();
        Marathon.log(Level.INFO, "Server object initialized!");
    }
}
