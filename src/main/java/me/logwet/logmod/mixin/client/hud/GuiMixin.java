package me.logwet.logmod.mixin.client.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.tools.overlay.OverlayRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "render", at = @At("RETURN"))
    private void onPostRenderGameOverlay(PoseStack poseStack, float f, CallbackInfo ci) {
        OverlayRenderer.onPostRenderGameOverlay(this.minecraft, poseStack, f);
    }
}
