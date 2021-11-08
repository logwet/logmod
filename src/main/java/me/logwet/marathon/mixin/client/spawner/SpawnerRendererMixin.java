package me.logwet.marathon.mixin.client.spawner;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.util.BoxRenderer;
import me.logwet.marathon.util.spawner.HoloTextRenderer;
import me.logwet.marathon.util.spawner.MatrixPointCloudRenderer;
import me.logwet.marathon.util.spawner.SpawnerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SpawnerRenderer.class)
public abstract class SpawnerRendererMixin extends BlockEntityRenderer<SpawnerBlockEntity> {
    public SpawnerRendererMixin(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(
            SpawnerBlockEntity spawnerBlockEntity,
            float f,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            int j,
            CallbackInfo ci) {
        SpawnerInfo spawnerInfo = Marathon.getSpawnerInfo(spawnerBlockEntity.getBlockPos());
        if (Marathon.shouldRender() && spawnerInfo != null) {
            StringBuilder numText = new StringBuilder();
            StringBuilder probText = new StringBuilder();

            for (int k = 0; k <= spawnerInfo.getNumTrials(); k++) {
                numText.append(k);
                probText.append(String.format("%.2f", spawnerInfo.getProbabilities()[k] * 100D));

                if (k < spawnerInfo.getNumTrials()) {
                    numText.append("      ");
                    probText.append(" ");
                }
            }

            Component[] components =
                    new Component[] {
                        new TextComponent("Avg #: ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(
                                        new TextComponent(
                                                        String.format("%.2f", spawnerInfo.getAvg()))
                                                .withStyle(ChatFormatting.AQUA)),
                        new TextComponent("#: ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(
                                        new TextComponent(numText.toString())
                                                .withStyle(ChatFormatting.YELLOW)),
                        new TextComponent("%: ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(
                                        new TextComponent(probText.toString())
                                                .withStyle(ChatFormatting.WHITE))
                    };

            HoloTextRenderer.renderTexts(
                    poseStack,
                    multiBufferSource,
                    this.renderer.camera.rotation(),
                    this.renderer.getFont(),
                    i,
                    new Vec3(0.0D, 0.5D, 0.0D),
                    components);

            MatrixPointCloudRenderer.renderMatrix(
                    poseStack,
                    multiBufferSource,
                    spawnerInfo.getProbMatrix(),
                    spawnerInfo.getBoundingBox(),
                    d -> d == 0.0D,
                    d -> new float[] {1.0F, 0.0F, 0.0F});

            Vec3 bbExpansionFactor =
                    new Vec3(
                            spawnerInfo.getEntityBoundingBox().getXsize() / 2D,
                            0.0D,
                            spawnerInfo.getEntityBoundingBox().getZsize() / 2D);

            AABB blockBoundingBox =
                    spawnerInfo
                            .getBoundingBox()
                            .expandTowards(bbExpansionFactor)
                            .expandTowards(bbExpansionFactor.scale(-1.0D))
                            .expandTowards(
                                    0.0D,
                                    Math.max(
                                            spawnerInfo.getEntityBoundingBox().getYsize() - 1,
                                            0.0D),
                                    0.0D);

            BoxRenderer.renderBox(
                    poseStack, multiBufferSource, blockBoundingBox, 1.0F, 0.65F, 0.0F);

            if (Marathon.shouldRenderOptional()) {
                BoxRenderer.renderBox(
                        poseStack,
                        multiBufferSource,
                        spawnerInfo.getBoundingBox(),
                        1.0F,
                        1.0F,
                        1.0F);

                MatrixPointCloudRenderer.renderMatrix(
                        poseStack,
                        multiBufferSource,
                        spawnerInfo.getProbMatrix(),
                        spawnerInfo.getBoundingBox(),
                        d -> d > 0.0D,
                        d -> {
                            double d2 = d / spawnerInfo.getMaxPossibleProb();
                            float r = 0.0F;
                            float g = (float) d2;
                            float b = (float) (1D - d2);

                            return new float[] {r, g, b};
                        });
            }
        }
    }
}
