package me.logwet.logmod.mixin.client.health;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.LogModData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(
            T livingEntity,
            float f,
            float g,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            CallbackInfo ci) {
        if (LogModData.isHealthEnabled()) {
            if (livingEntity instanceof AbstractClientPlayer) {
                if (!this.shouldShowName(livingEntity)) {
                    return;
                }
            }

            float health = livingEntity.getHealth();

            if (health < livingEntity.getMaxHealth()) {
                Component message =
                        new TextComponent(String.format("%.2f", health) + " ♥")
                                .withStyle(ChatFormatting.RED);
                this.renderNameTag(livingEntity, message, poseStack, multiBufferSource, i);
            }
        }
    }
}
