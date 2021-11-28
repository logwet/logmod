package me.logwet.logmod.tools.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class OverlayRenderer {
    private static final List<RenderOverlay> renderOverlays = new ArrayList<>();

    public static void registerRenderer(RenderOverlay overlay) {
        renderOverlays.add(overlay);
    }

    public static void onPostRenderGameOverlay(
            Minecraft MC, PoseStack poseStack, float partialTicks) {
        MC.getProfiler().push(LogMod.MODID + "_renderhud");

        if (LogModData.isHudEnabled()) {
            for (RenderOverlay overlay : renderOverlays) {
                MC.getProfiler().push(overlay.getProfilerSectionSupplier());

                overlay.onPostRenderGameOverlay(MC, poseStack, partialTicks);

                MC.getProfiler().pop();
            }
        }

        MC.getProfiler().pop();
    }
}
