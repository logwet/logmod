package me.logwet.marathon;

import me.logwet.marathon.commands.MarathonCommand;
import me.logwet.marathon.commands.server.RodsCommand;
import me.logwet.marathon.commands.server.SpawnerCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        MarathonCommand.registerServerCommand(SpawnerCommand.INSTANCE);
        MarathonCommand.registerServerCommand(RodsCommand.INSTANCE);
        CommandRegistrationCallback.EVENT.register(MarathonCommand::registerServer);

        log(Level.INFO, "Main class initialized!");
    }
}
