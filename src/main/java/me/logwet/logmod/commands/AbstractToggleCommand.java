package me.logwet.logmod.commands;

import com.mojang.brigadier.context.CommandContext;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class AbstractToggleCommand<T extends SharedSuggestionProvider> {
    protected final String root;
    protected final String description;
    protected final Supplier<Boolean> statusSupplier;

    public AbstractToggleCommand(
            String root, String description, Supplier<Boolean> statusSupplier) {
        this.root = root;
        this.description = description;
        this.statusSupplier = statusSupplier;
    }

    protected Component buildComponent() {
        boolean status = statusSupplier.get();

        return new TextComponent("The " + description + " has been ")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(
                        new TextComponent(status ? "enabled" : "disabled")
                                .withStyle(status ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    protected abstract int toggle(CommandContext<T> context);

    public String getRoot() {
        return root;
    }
}
