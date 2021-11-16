package me.logwet.logmod.tools.overlay.trajectories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

public class TrajectoryRenderer {

    public static void renderTrajectory(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            double x,
            double y,
            double z,
            Trajectory trajectory) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix4f = poseStack.last().pose();

        for (int i = 5; i < trajectory.getTrajectory().size(); i++) {
            Vec3 prevPos = trajectory.getTrajectory().get(i - 1);
            Vec3 pos = trajectory.getTrajectory().get(i);

            vertexConsumer
                    .vertex(
                            matrix4f,
                            (float) (prevPos.x - x),
                            (float) (prevPos.y - y),
                            (float) (prevPos.z - z))
                    .color(0.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();

            vertexConsumer
                    .vertex(matrix4f, (float) (pos.x - x), (float) (pos.y - y), (float) (pos.z - z))
                    .color(0.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();
        }
    }
}
