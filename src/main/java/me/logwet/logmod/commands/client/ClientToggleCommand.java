package me.logwet.logmod.commands.client;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Supplier;
import me.logwet.logmod.commands.AbstractToggleCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public class ClientToggleCommand extends AbstractToggleCommand<FabricClientCommandSource>
        implements ClientCommand {
    public ClientToggleCommand(String root, String description, Supplier<Boolean> statusSupplier) {
        super(root, description, statusSupplier);
    }

    @Override
    protected int toggle(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(buildComponent());

        return 1;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder() {
        return literal(root).executes(this::toggle);
    }
}
