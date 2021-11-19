package me.logwet.logmod.mixin.client.trajectories;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.BoxRenderer;
import me.logwet.logmod.tools.trajectories.Trajectory;
import me.logwet.logmod.tools.trajectories.TrajectoryRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            double x,
            double y,
            double z,
            CallbackInfo ci) {
        UUID uuid = Minecraft.getInstance().gameRenderer.getMainCamera().getEntity().getUUID();

        Trajectory trajectory;

        if (LogMod.shouldRender()
                && LogModData.isProjectilesEnabled()
                && (trajectory = LogModData.getTrajectory(uuid)) != null) {
            Vec3 playerPos = new Vec3(x, y, z);

            TrajectoryRenderer.renderTrajectory(poseStack, bufferSource, playerPos, trajectory);

            BlockHitResult blockHitResult = trajectory.getBlockHitResult();

            if (blockHitResult != null) {
                BlockPos blockPos = blockHitResult.getBlockPos();

                BoxRenderer.renderBox(
                        poseStack,
                        bufferSource,
                        AABB.unitCubeFromLowerCorner(
                                Vec3.atLowerCornerOf(blockPos).subtract(playerPos)),
                        1.0F,
                        0.0F,
                        1.0F);
            }
        }
    }
}
