package me.logwet.marathon.mixin.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.logwet.marathon.commands.MarathonCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(
            method = "<init>",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V",
                            remap = false))
    private void addCommands(Commands.CommandSelection commandSelection, CallbackInfo ci) {
        MarathonCommand.register(
                this.dispatcher, commandSelection == Commands.CommandSelection.DEDICATED);
    }
}
