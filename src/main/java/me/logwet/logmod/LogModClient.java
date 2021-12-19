package me.logwet.logmod;

import com.mojang.blaze3d.platform.InputConstants.Type;
import me.logwet.logmod.commands.RootCommand;
import me.logwet.logmod.commands.client.ClientToggleCommand;
import me.logwet.logmod.tools.hud.PlayerAttributeRenderer;
import me.logwet.logmod.tools.hud.SpeedRenderer;
import me.logwet.logmod.tools.hud.WeaponAttributeRenderer;
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
        OverlayRenderer.registerRenderer(new PlayerAttributeRenderer());
        OverlayRenderer.registerRenderer(new WeaponAttributeRenderer());

        RootCommand.registerClientCommand(
                new ClientToggleCommand(
                        "renderSpawner",
                        "spawner info renderer",
                        LogModData::toggleRenderSpawnersEnabled));
        RootCommand.registerClientCommand(
                new ClientToggleCommand("hud", "info HUD", LogModData::toggleHudEnabled));
        RootCommand.registerClientCommand(
                new ClientToggleCommand(
                        "projectiles",
                        "projectiles renderer",
                        LogModData::toggleProjectilesEnabled));
        RootCommand.registerClientCommand(
                new ClientToggleCommand("paths", "paths renderer", LogModData::togglePathsEnabled));
        RootCommand.registerClientCommand(
                new ClientToggleCommand(
                        "piglins", "piglins renderer", LogModData::togglePiglinsEnabled));
        RootCommand.registerClientCommand(
                new ClientToggleCommand(
                        "health", "health renderer", LogModData::toggleHealthEnabled));

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
