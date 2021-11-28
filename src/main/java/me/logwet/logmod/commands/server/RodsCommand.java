package me.logwet.logmod.commands.server;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class RodsCommand implements ServerCommand {
    public static final RodsCommand INSTANCE = new RodsCommand();
    protected static final String root = "rods";

    private static int getTargetRods(CommandContext<CommandSourceStack> context) {
        int value = LogModData.getTargetRods();

        context.getSource().sendSuccess(buildGetMessage("targetRods", value, "", 0), true);

        return 1;
    }

    private static int setTargetRods(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "num");

        LogModData.setTargetRods(value);

        context.getSource().sendSuccess(buildSetMessage("targetRods", value, "", 0), true);

        return 1;
    }

    private static int getTargetTime(CommandContext<CommandSourceStack> context) {
        double value = LogModData.getTargetTime();

        context.getSource().sendSuccess(buildGetMessage("targetTime", value, "s", 2), true);

        return 1;
    }

    private static int setTargetTime(CommandContext<CommandSourceStack> context) {
        int rawValue = IntegerArgumentType.getInteger(context, "time");
        double value = (double) rawValue / 20.0D;

        LogModData.setTargetTime(value);

        context.getSource().sendSuccess(buildSetMessage("targetTime", value, "s", 2), true);

        return 1;
    }

    private static int getLootingLevel(CommandContext<CommandSourceStack> context) {
        int value = LogModData.getLootingLevel();

        context.getSource().sendSuccess(buildGetMessage("lootingLevel", value, "", 0), true);

        return 1;
    }

    private static int setLootingLevel(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "level");

        LogModData.setLootingLevel(value);

        context.getSource().sendSuccess(buildSetMessage("lootingLevel", value, "", 0), true);

        return 1;
    }

    private static Component buildGetMessage(String name, double value, String unit, int places) {
        return new TextComponent(name + " is ")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(
                        new TextComponent(LogMod.roundToString(value, places))
                                .withStyle(ChatFormatting.GOLD))
                .append(new TextComponent(unit).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static Component buildSetMessage(String name, double value, String unit, int places) {
        return new TextComponent("Set " + name + " to ")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(
                        new TextComponent(LogMod.roundToString(value, places))
                                .withStyle(ChatFormatting.GOLD))
                .append(new TextComponent(unit).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(boolean dedicated) {
        return literal(root)
                .then(
                        literal("targetRods")
                                .executes(RodsCommand::getTargetRods)
                                .requires(source -> source.hasPermission(1))
                                .then(
                                        argument("num", IntegerArgumentType.integer(1, 16))
                                                .executes(RodsCommand::setTargetRods)))
                .then(
                        literal("targetTime")
                                .executes(RodsCommand::getTargetTime)
                                .requires(source -> source.hasPermission(1))
                                .then(
                                        argument("time", TimeArgument.time())
                                                .executes(RodsCommand::setTargetTime)))
                .then(
                        literal("lootingLevel")
                                .executes(RodsCommand::getLootingLevel)
                                .requires(source -> source.hasPermission(1))
                                .then(
                                        argument("level", IntegerArgumentType.integer(0, 3))
                                                .executes(RodsCommand::setLootingLevel)));
    }
}
