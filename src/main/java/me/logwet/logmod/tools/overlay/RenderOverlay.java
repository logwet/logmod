package me.logwet.logmod.tools.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public interface RenderOverlay {
    default void update(Minecraft MC) {}

    void onPostRenderGameOverlay(Minecraft MC, PoseStack poseStack, float partialTicks);

    default Supplier<String> getProfilerSectionSupplier() {
        return () -> this.getClass().getName();
    }
}
