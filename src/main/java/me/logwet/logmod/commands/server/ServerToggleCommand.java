package me.logwet.logmod.commands.server;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Supplier;
import me.logwet.logmod.commands.AbstractToggleCommand;
import net.minecraft.commands.CommandSourceStack;

public class ServerToggleCommand extends AbstractToggleCommand<CommandSourceStack>
        implements ServerCommand {
    public ServerToggleCommand(String root, String description, Supplier<Boolean> statusSupplier) {
        super(root, description, statusSupplier);
    }

    @Override
    protected int toggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(buildComponent(), false);

        return 0;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(boolean dedicated) {
        return literal(root).executes(this::toggle);
    }
}
