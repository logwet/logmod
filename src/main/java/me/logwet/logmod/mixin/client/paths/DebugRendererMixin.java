package me.logwet.logmod.mixin.client.paths;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.paths.ItemTrigger;
import me.logwet.logmod.tools.paths.PathContainer;
import me.logwet.logmod.tools.paths.PathRenderer;
import me.logwet.logmod.tools.paths.PathSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            long seed =
                    Minecraft.getInstance()
                            .getSingleplayerServer()
                            .getWorldData()
                            .worldGenSettings()
                            .seed();

            PathSet pathSet;

            if (LogMod.shouldRender()
                    && LogModData.isPathsEnabled()
                    && (pathSet = LogModData.getPathSet(seed)) != null) {
                Vec3 playerPos = new Vec3(x, y, z);

                for (ItemTrigger trigger : ItemTrigger.values()) {
                    PathContainer pathContainer = pathSet.getPath(trigger);

                    if (pathContainer != null) {
                        PathRenderer.renderPath(poseStack, bufferSource, playerPos, pathContainer);
                    }
                }
            }
        }
    }
}
