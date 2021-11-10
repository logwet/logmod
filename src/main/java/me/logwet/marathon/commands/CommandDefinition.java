package me.logwet.marathon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;

public interface CommandDefinition {
    LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(boolean dedicated);

    LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder();
}
