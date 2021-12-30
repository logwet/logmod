package me.logwet.logmod;

import me.logwet.delorean.DeLorean;
import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.server.RodsCommand;
import me.logwet.logmod.commands.server.SpawnerCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogMod implements ModInitializer {
    public static final String MODID = "logmod";
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

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MODID + " v" + VERSION + "] " + message);
    }

    @Environment(EnvType.CLIENT)
    public static boolean isInSingleplayer() {
        if (IS_CLIENT) {
            return Minecraft.getInstance().hasSingleplayerServer();
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static boolean shouldRender() {
        return isInSingleplayer() && !Minecraft.getInstance().showOnlyReducedInfo();
    }

    @Environment(EnvType.CLIENT)
    public static boolean shouldRenderOptional() {
        return Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes();
    }

    public static String roundToString(double value) {
        return roundToString(value, 2);
    }

    public static String roundToString(double value, int places) {
        return String.format("%." + places + "f", value);
    }

    @Override
    public void onInitialize() {
        DeLorean.ENABLED = true;
        DeLorean.CONTROL_ENABLED = true;

        RootCommand.registerServerCommand(SpawnerCommand.INSTANCE);
        RootCommand.registerServerCommand(RodsCommand.INSTANCE);
        CommandRegistrationCallback.EVENT.register(RootCommand::registerServer);

        log(Level.INFO, "Main class initialized!");
    }
}
