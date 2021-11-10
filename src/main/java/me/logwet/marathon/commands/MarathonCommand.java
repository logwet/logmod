package me.logwet.marathon.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.commands.client.HudCommand;
import me.logwet.marathon.commands.server.SpawnerCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MarathonCommand {
    private static final CommandDefinition[] serverCommands =
            new CommandDefinition[] {SpawnerCommand.INSTANCE};

    @Environment(EnvType.CLIENT)
    private static final CommandDefinition[] clientCommands =
            new CommandDefinition[] {HudCommand.INSTANCE};

    public static void registerServer(
            CommandDispatcher<CommandSourceStack> commandDispatcher, boolean dedicated) {
        for (CommandDefinition command : serverCommands) {
            commandDispatcher.register(
                    Commands.literal(Marathon.MODID).then(command.getCommandBuilder(dedicated)));
            commandDispatcher.register(command.getCommandBuilder(dedicated));
        }
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(
            CommandDispatcher<FabricClientCommandSource> commandDispatcher) {
        for (CommandDefinition command : clientCommands) {
            commandDispatcher.register(
                    ClientCommandManager.literal(Marathon.MODID + "client")
                            .then(command.getCommandBuilder()));
            commandDispatcher.register(command.getCommandBuilder());
        }
    }
}
