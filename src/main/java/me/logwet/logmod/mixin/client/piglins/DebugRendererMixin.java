package me.logwet.logmod.mixin.client.piglins;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.BoxRenderer;
import me.logwet.logmod.tools.piglins.PiglinAggroRange;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
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
        Entity cameraEntity = Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
        UUID uuid = cameraEntity.getUUID();

        PiglinAggroRange piglinAggroRange;

        if (LogMod.shouldRender() && (piglinAggroRange = LogModData.getAggroRange(uuid)) != null) {
            Vec3 playerPos = new Vec3(x, y, z);

            if (piglinAggroRange.getBlockTarget() != null) {
                BoxRenderer.renderBox(
                        poseStack,
                        bufferSource,
                        AABB.unitCubeFromLowerCorner(
                                Vec3.atLowerCornerOf(piglinAggroRange.getBlockTarget())
                                        .subtract(playerPos)),
                        1.0F,
                        0.0F,
                        1.0F);
            }

            if (piglinAggroRange.getRange() != null) {
                BoxRenderer.renderBox(
                        poseStack, bufferSource, piglinAggroRange.getRange(), 0.0F, 1.0F, 1.0F);
            }
        }
    }
}
