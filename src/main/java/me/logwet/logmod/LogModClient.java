package me.logwet.logmod;

import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.client.HudCommand;
import me.logwet.logmod.commands.client.RenderSpawnerCommand;
import me.logwet.logmod.tools.hud.AttributeRenderer;
import me.logwet.logmod.tools.hud.HudRenderer;
import me.logwet.logmod.tools.hud.SpeedRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import org.apache.logging.log4j.Level;

@Environment(EnvType.CLIENT)
public class LogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderer.registerRenderer(new SpeedRenderer());
        HudRenderer.registerRenderer(new AttributeRenderer());

        RootCommand.registerClientCommand(RenderSpawnerCommand.INSTANCE);
        RootCommand.registerClientCommand(HudCommand.INSTANCE);
        RootCommand.registerClient(ClientCommandManager.DISPATCHER);

        LogMod.log(Level.INFO, "Client class initialized!");
    }
}
