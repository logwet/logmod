package me.logwet.marathon.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class BoxRenderer {
    public static void renderBox(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            AABB boundingBox,
            float r,
            float g,
            float b) {
        poseStack.pushPose();

        LevelRenderer.renderLineBox(
                poseStack,
                multiBufferSource.getBuffer(RenderType.lines()),
                boundingBox,
                r,
                g,
                b,
                1.0F);

        poseStack.popPose();
    }
}
