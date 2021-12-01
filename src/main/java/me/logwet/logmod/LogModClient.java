package me.logwet.logmod;

import com.mojang.blaze3d.platform.InputConstants.Type;
import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.client.HudCommand;
import me.logwet.logmod.commands.client.PathsCommand;
import me.logwet.logmod.commands.client.PiglinsCommand;
import me.logwet.logmod.commands.client.ProjectilesCommand;
import me.logwet.logmod.commands.client.RenderSpawnerCommand;
import me.logwet.logmod.tools.hud.AttributeRenderer;
import me.logwet.logmod.tools.hud.SpeedRenderer;
import me.logwet.logmod.tools.overlay.OverlayRenderer;
import me.logwet.logmod.tools.paths.PathHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class LogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OverlayRenderer.registerRenderer(new SpeedRenderer());
        OverlayRenderer.registerRenderer(new AttributeRenderer());

        RootCommand.registerClientCommand(RenderSpawnerCommand.INSTANCE);
        RootCommand.registerClientCommand(HudCommand.INSTANCE);
        RootCommand.registerClientCommand(ProjectilesCommand.INSTANCE);
        RootCommand.registerClientCommand(PathsCommand.INSTANCE);
        RootCommand.registerClientCommand(PiglinsCommand.INSTANCE);
        RootCommand.registerClient(ClientCommandManager.DISPATCHER);

        KeyMapping buildPathKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyMapping(
                                "key.logmod.path.build",
                                Type.KEYSYM,
                                GLFW.GLFW_KEY_INSERT,
                                "key.category.logmod.path"));

        KeyMapping deletePathKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyMapping(
                                "key.logmod.path.delete",
                                Type.KEYSYM,
                                GLFW.GLFW_KEY_END,
                                "key.category.logmod.path"));

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {
                    if (client.player != null) {
                        while (buildPathKey.consumeClick()) {
                            PathHandler.buildPath(client);

                            client.player.displayClientMessage(
                                    new TextComponent("Adding node to path!"), false);
                        }

                        while (deletePathKey.consumeClick()) {
                            PathHandler.deletePath(client);

                            client.player.displayClientMessage(
                                    new TextComponent("Deleting path!"), false);
                        }
                    }
                });

        LogMod.log(Level.INFO, "Client class initialized!");
    }
}
