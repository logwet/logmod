package me.logwet.marathon.mixin.client;

import me.logwet.marathon.Marathon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DebugScreenOverlay.class)
public abstract class DebugHudMixin {
    /**
     * @author DuncanRuns
     * @reason Puts mod notice in F3 menu
     */
    @Inject(at = @At("RETURN"), method = "getGameInformation")
    private void injectGetLeftText(CallbackInfoReturnable<List<String>> info) {
        info.getReturnValue().add(Marathon.MODID + " mod v" + Marathon.VERSION + " by logwet");
    }
}
