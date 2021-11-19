package me.logwet.logmod;

import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.client.HudCommand;
import me.logwet.logmod.commands.client.PiglinsCommand;
import me.logwet.logmod.commands.client.ProjectilesCommand;
import me.logwet.logmod.commands.client.RenderSpawnerCommand;
import me.logwet.logmod.tools.hud.AttributeRenderer;
import me.logwet.logmod.tools.hud.SpeedRenderer;
import me.logwet.logmod.tools.overlay.OverlayRenderer;
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

        RootCommand.registerClientCommand(RenderSpawnerCommand.INSTANCE);
        RootCommand.registerClientCommand(HudCommand.INSTANCE);
        RootCommand.registerClientCommand(ProjectilesCommand.INSTANCE);
        RootCommand.registerClientCommand(PiglinsCommand.INSTANCE);
        RootCommand.registerClient(ClientCommandManager.DISPATCHER);

        LogMod.log(Level.INFO, "Client class initialized!");
    }
}
