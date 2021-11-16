package me.logwet.marathon.commands.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public interface ClientCommand {
    String getRoot();

    LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder();
}
