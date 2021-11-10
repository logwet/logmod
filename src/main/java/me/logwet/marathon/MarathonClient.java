package me.logwet.marathon;

import me.logwet.marathon.commands.MarathonCommand;
import me.logwet.marathon.commands.client.HudCommand;
import me.logwet.marathon.util.hud.AttributeRenderer;
import me.logwet.marathon.util.hud.HudRenderer;
import me.logwet.marathon.util.hud.SpeedRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import org.apache.logging.log4j.Level;

@Environment(EnvType.CLIENT)
public class MarathonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderer.registerRenderer(new SpeedRenderer());
        HudRenderer.registerRenderer(new AttributeRenderer());

        MarathonCommand.registerClientCommand(HudCommand.INSTANCE);
        MarathonCommand.registerClient(ClientCommandManager.DISPATCHER);

        Marathon.log(Level.INFO, "Client class initialized!");
    }
}
