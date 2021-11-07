package me.logwet.marathon.mixin.client.spawner;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.util.HoloTextRenderer;
import me.logwet.marathon.util.SpawnerInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        if (Marathon.inSinglePlayer() && spawnerInfo != null) {
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
        }
    }
}