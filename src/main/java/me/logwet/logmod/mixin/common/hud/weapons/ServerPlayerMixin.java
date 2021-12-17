package me.logwet.logmod.mixin.common.hud.weapons;

import com.mojang.authlib.GameProfile;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.hud.PlayerAttribute;
import me.logwet.logmod.tools.hud.WeaponAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos blockPos, GameProfile gameProfile) {
        super(level, blockPos, gameProfile);
    }

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/player/Player;tick()V",
                            shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (LogMod.IS_CLIENT && LogModData.isHudEnabled()) {
            LogModData.addPlayerAttribute(
                    this.getUUID(),
                    new PlayerAttribute(
                            this.getHealth(),
                            this.foodData.getFoodLevel(),
                            this.foodData.getSaturationLevel()));

            float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            float enchantModifier =
                    EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);

            float attackStrength = this.getAttackStrengthScale(0.5F);

            attackDamage *= 0.2F + attackStrength * attackStrength * 0.8F;
            enchantModifier *= attackStrength;

            boolean isCharged = attackStrength > 0.9F;
            boolean isFalling =
                    this.fallDistance > 0.0F
                            && !this.onGround
                            && !this.onClimbable()
                            && !this.isInWater()
                            && !this.hasEffect(MobEffects.BLINDNESS)
                            && !this.isPassenger()
                            && !this.isSprinting();
            boolean isCrit = isCharged && isFalling;

            if (isCrit) {
                attackDamage *= 1.5F;
            }

            attackDamage += enchantModifier;

            float chargePercent = this.getAttackStrengthScale(0.0F);

            LogModData.addWeaponAttribute(
                    this.getUUID(),
                    new WeaponAttribute(attackDamage, chargePercent, isCharged, isFalling, isCrit));
        }
    }
}
