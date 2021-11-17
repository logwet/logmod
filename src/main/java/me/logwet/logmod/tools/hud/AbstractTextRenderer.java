package me.logwet.logmod.tools.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.logwet.logmod.tools.overlay.RenderOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractTextRenderer implements RenderOverlay {
    private final HudAlignment hudAlignment;
    protected List<String> lines = new ArrayList<>();
    private long infoUpdateTime;

    public AbstractTextRenderer(HudAlignment hudAlignment) {
        this.hudAlignment = hudAlignment;
    }

    protected void addLine(String line) {
        this.lines.add(line);
    }

    @Override
    public void onPostRenderGameOverlay(Minecraft MC, PoseStack poseStack, float partialTicks) {
        if (!MC.options.renderDebug && MC.player != null && !MC.options.hideGui) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - this.infoUpdateTime >= 50) {
                this.update(MC);
                this.infoUpdateTime = currentTime;
            }

            int x = 4;
            int y = 4;
            double fontScale = 1.0D;
            int textColor = StringUtils.getColor("#FFE0E0E0", 0);
            int bgColor = StringUtils.getColor("#A0505050", 0);
            boolean useBackground = true;
            boolean useShadow = false;

            RenderUtils.renderText(
                    x,
                    y,
                    fontScale,
                    textColor,
                    bgColor,
                    this.hudAlignment,
                    useBackground,
                    useShadow,
                    this.lines,
                    poseStack);
        }
    }
}
