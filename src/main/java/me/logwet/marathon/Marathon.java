package me.logwet.marathon;

import me.logwet.marathon.util.SpawnerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.cayenne.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentMap;

public class Marathon implements ModInitializer {
    public static final String MODID = "marathon";
    public static final String VERSION =
            FabricLoader.getInstance()
                    .getModContainer(MODID)
                    .get()
                    .getMetadata()
                    .getVersion()
                    .getFriendlyString();
    public static final boolean IS_CLIENT =
            FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final ConcurrentMap<BlockPos, SpawnerInfo> spawnerInfoMap =
            new ConcurrentLinkedHashMap.Builder<BlockPos, SpawnerInfo>()
                    .concurrencyLevel(2)
                    .maximumWeightedCapacity(32)
                    .build();
    private static MinecraftServer MS;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MODID + " v" + VERSION + "] " + message);
    }

    public static MinecraftServer getMS() {
        return MS;
    }

    private static void setMS(MinecraftServer MS) {
        Marathon.MS = MS;
    }

    private static ConcurrentMap<BlockPos, SpawnerInfo> getSpawnerInfoMap() {
        return spawnerInfoMap;
    }

    @Environment(EnvType.CLIENT)
    public static boolean hasSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoMap().containsKey(blockPos);
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static SpawnerInfo getSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoMap().get(blockPos);
    }

    public static boolean addSpawnerInfo(BlockPos blockPos, SpawnerInfo spawnerInfo) {
        return getSpawnerInfoMap().put(blockPos, spawnerInfo) == null;
    }

    public static boolean removeSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoMap().remove(blockPos) != null;
    }

    public static void onServerInit(MinecraftServer ms) {
        setMS(ms);
        getSpawnerInfoMap().clear();
        log(Level.INFO, "Server object initialized!");
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Main class initialized!");
    }
}
