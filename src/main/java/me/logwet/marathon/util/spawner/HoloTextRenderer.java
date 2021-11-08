package me.logwet.marathon.util.spawner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class HoloTextRenderer {
    public static void renderText(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            Quaternion cameraRotation,
            Font font,
            int i,
            int v,
            Vec3 transform,
            Component component) {
        poseStack.pushPose();

        poseStack.translate(0.5D + transform.x, 1.5D + transform.y, 0.5D + transform.z);
        poseStack.mulPose(cameraRotation);
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = poseStack.last().pose();

        float g = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int k = (int) (g * 255.0F) << 24;
        float h = (float) (-font.width(component) / 2);

        font.drawInBatch(
                component, h, v, 553648127, false, matrix4f, multiBufferSource, true, k, i);

        font.drawInBatch(component, h, v, -1, false, matrix4f, multiBufferSource, false, 0, i);

        poseStack.popPose();
    }

    private static int getHeightOffset(int i, int h, int b) {
        return i * (h + b);
    }

    public static void renderTexts(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            Quaternion cameraRotation,
            Font font,
            int i,
            Vec3 transform,
            Component[] components) {

        for (int j = 0; j < components.length; j++) {
            renderText(
                    poseStack,
                    multiBufferSource,
                    cameraRotation,
                    font,
                    i,
                    getHeightOffset(j, font.lineHeight, 1),
                    transform,
                    components[j]);
        }
    }
}
