package me.logwet.marathon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class MarathonCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> commandDispatcher, boolean dedicated) {
        LiteralCommandNode<CommandSourceStack> spawnerCommandNode =
                registerSpawnerCommand(commandDispatcher);
        commandDispatcher.register(
                literal("marathon").then(literal("spawner").redirect(spawnerCommandNode)));
    }

    private static LiteralCommandNode<CommandSourceStack> registerSpawnerCommand(
            CommandDispatcher<CommandSourceStack> dispatcher) {
        return dispatcher.register(
                literal("spawner")
                        .executes(SpawnerCommand::run)
                        .then(literal("analyse").executes(SpawnerCommand::run))
                        .then(literal("toggleSpawners").executes(SpawnerCommand::toggleSpawning))
                        .then(literal("toggleAnalysis").executes(SpawnerCommand::toggleAnalysis)));
    }
}
