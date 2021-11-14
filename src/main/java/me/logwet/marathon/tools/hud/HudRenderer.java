package me.logwet.marathon.tools.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.marathon.MarathonData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class HudRenderer {
    private static final List<IRenderOverlay> renderOverlays = new ArrayList<>();

    public static void registerRenderer(IRenderOverlay overlay) {
        renderOverlays.add(overlay);
    }

    public static void onPostRenderGameOverlay(
            Minecraft MC, PoseStack poseStack, float partialTicks) {
        MC.getProfiler().push("marathon_renderhud");

        if (MarathonData.isHudEnabled()) {
            for (IRenderOverlay overlay : renderOverlays) {
                MC.getProfiler().push(overlay.getProfilerSectionSupplier());

                overlay.onPostRenderGameOverlay(MC, poseStack, partialTicks);

                MC.getProfiler().pop();
            }
        }

        MC.getProfiler().pop();
    }
}
