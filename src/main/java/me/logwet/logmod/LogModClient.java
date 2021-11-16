package me.logwet.logmod;

import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.client.HudCommand;
import me.logwet.logmod.commands.client.RenderSpawnerCommand;
import me.logwet.logmod.tools.overlay.OverlayRenderer;
import me.logwet.logmod.tools.overlay.hud.AttributeRenderer;
import me.logwet.logmod.tools.overlay.hud.SpeedRenderer;
import me.logwet.logmod.tools.overlay.trajectories.TrajectoryRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import org.apache.logging.log4j.Level;

@Environment(EnvType.CLIENT)
public class LogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OverlayRenderer.registerRenderer(new SpeedRenderer());
        OverlayRenderer.registerRenderer(new AttributeRenderer());
        OverlayRenderer.registerRenderer(new TrajectoryRenderer());

        RootCommand.registerClientCommand(RenderSpawnerCommand.INSTANCE);
        RootCommand.registerClientCommand(HudCommand.INSTANCE);
        RootCommand.registerClient(ClientCommandManager.DISPATCHER);

        LogMod.log(Level.INFO, "Client class initialized!");
    }
}
