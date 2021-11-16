package me.logwet.marathon.commands.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.marathon.MarathonData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class HudCommand implements ClientCommand {
    public static final HudCommand INSTANCE = new HudCommand();
    protected static final String root = "hud";

    private static int toggle(CommandContext<FabricClientCommandSource> context) {
        boolean status = MarathonData.toggleHudEnabled();

        context.getSource()
                .sendFeedback(
                        new TextComponent("The info HUD has been ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(
                                        new TextComponent(status ? "enabled" : "disabled")
                                                .withStyle(
                                                        status
                                                                ? ChatFormatting.GREEN
                                                                : ChatFormatting.RED)));

        return 1;
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder() {
        return literal(root).executes(HudCommand::toggle);
    }
}
