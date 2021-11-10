package me.logwet.marathon.commands.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.marathon.commands.CommandDefinition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class HudCommand implements CommandDefinition {
    public static final HudCommand INSTANCE = new HudCommand();

    private static int toggle(CommandContext<FabricClientCommandSource> context) {
        Player player = context.getSource().getPlayer();
        context.getSource().sendFeedback(new TextComponent("Ran HUD command."));
        return 1;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(boolean dedicated) {
        return null;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder() {
        return ClientCommandManager.literal("hud").executes(HudCommand::toggle);
    }
}
