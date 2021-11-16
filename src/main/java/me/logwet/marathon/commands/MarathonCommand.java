package me.logwet.marathon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.commands.client.ClientCommand;
import me.logwet.marathon.commands.server.ServerCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public class MarathonCommand {
    private static final List<ServerCommand> serverCommands = new ArrayList<>();

    @Environment(EnvType.CLIENT)
    private static final List<ClientCommand> clientCommands = new ArrayList<>();

    public static void registerServerCommand(ServerCommand command) {
        serverCommands.add(command);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientCommand(ClientCommand command) {
        clientCommands.add(command);
    }

    public static void registerServer(
            CommandDispatcher<CommandSourceStack> commandDispatcher, boolean dedicated) {
        LiteralArgumentBuilder<CommandSourceStack> rootCommand = Commands.literal(Marathon.MODID);

        for (ServerCommand command : serverCommands) {
            rootCommand.then(command.getCommandBuilder(dedicated));
            commandDispatcher.register(command.getCommandBuilder(dedicated));
        }

        commandDispatcher.register(rootCommand);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(
            CommandDispatcher<FabricClientCommandSource> commandDispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> rootCommand =
                ClientCommandManager.literal(Marathon.MODID + "client");

        for (ClientCommand command : clientCommands) {
            rootCommand.then(command.getCommandBuilder());
            commandDispatcher.register(command.getCommandBuilder());
        }

        commandDispatcher.register(rootCommand);
    }
}
