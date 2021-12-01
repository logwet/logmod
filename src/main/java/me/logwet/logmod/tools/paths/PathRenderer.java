package me.logwet.logmod.tools.paths;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.List;
import me.logwet.logmod.tools.trajectories.TrajectoryRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

public class PathRenderer {
    public static void renderPath(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            Vec3 entityPos,
            PathContainer pathContainer) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix4f = poseStack.last().pose();

        List<Vec3> nodes = pathContainer.getNodes();

        for (int i = 1; i < nodes.size(); i++) {
            Vec3 prevPos = nodes.get(i - 1).subtract(entityPos);
            Vec3 pos = nodes.get(i).subtract(entityPos);

            Float[] RGB = pathContainer.getItemTrigger().getRGB();

            float r = RGB[0];
            float g = RGB[1];
            float b = RGB[2];

            TrajectoryRenderer.drawLine(vertexConsumer, matrix4f, prevPos, pos, r, g, b);
        }
    }
}
