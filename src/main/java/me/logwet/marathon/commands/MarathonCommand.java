package me.logwet.marathon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class MarathonCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> commandDispatcher, boolean dedicated) {
        commandDispatcher.register(
                literal("marathon").then(literal("spawner").executes(SpawnerCommand::run)));
    }
}
