package me.logwet.marathon.util.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface IRenderOverlay {
    default void update(Minecraft MC) {}

    void onPostRenderGameOverlay(Minecraft MC, PoseStack poseStack, float partialTicks);

    default Supplier<String> getProfilerSectionSupplier() {
        return () -> this.getClass().getName();
    }
}
