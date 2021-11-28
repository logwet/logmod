package me.logwet.logmod.tools.spawner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class MatrixPointCloudRenderer {
    public static void renderMatrix(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            double[][][] matrix,
            AABB boundingBox,
            Predicate<Double> predicate,
            Function<Double, float[]> colorSupplier) {
        final float pointSize = 0.003F;

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix4f = poseStack.last().pose();

        float hBound = (float) boundingBox.getXsize();
        float vBound = (float) boundingBox.getYsize();

        int matrixWidth = matrix.length;
        int matrixHeight = matrix[0].length;

        double v;

        for (int x0 = 0; x0 < matrixWidth; x0++) {
            for (int y0 = 0; y0 < matrixHeight; y0++) {
                for (int z0 = 0; z0 < matrixWidth; z0++) {
                    if (predicate.test((v = matrix[x0][y0][z0]))) {
                        float[] colorArray = colorSupplier.apply(v);
                        float r = colorArray[0];
                        float g = colorArray[1];
                        float b = colorArray[2];

                        float x = hBound * ((float) x0 / (matrixWidth - 1)) - (hBound / 2F) + 0.5F;
                        float y =
                                (vBound - 1F) * ((float) y0 / (matrixHeight - 1))
                                        - ((vBound - 1F) / 2F)
                                        + 0.5F;
                        float z = hBound * ((float) z0 / (matrixWidth - 1)) - (hBound / 2F) + 0.5F;

                        vertexConsumer
                                .vertex(matrix4f, x - pointSize, y, z)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                        vertexConsumer
                                .vertex(matrix4f, x + pointSize, y, z)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                        vertexConsumer
                                .vertex(matrix4f, x, y - pointSize, z)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                        vertexConsumer
                                .vertex(matrix4f, x, y + pointSize, z)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                        vertexConsumer
                                .vertex(matrix4f, x, y, z - pointSize)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                        vertexConsumer
                                .vertex(matrix4f, x, y, z + pointSize)
                                .color(r, g, b, 1.0F)
                                .endVertex();
                    }
                }
            }
        }
    }
}
