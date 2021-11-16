package me.logwet.logmod.mixin.common.hud.attributes;

import com.mojang.authlib.GameProfile;
import me.logwet.logmod.Marathon;
import me.logwet.logmod.MarathonData;
import me.logwet.logmod.tools.hud.PlayerAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
                            target =
                                    "Lnet/minecraft/network/protocol/game/ClientboundSetHealthPacket;<init>(FIF)V",
                            shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (Marathon.IS_CLIENT) {
            MarathonData.addPlayerAttribute(
                    this.getUUID(),
                    new PlayerAttribute(
                            this.getHealth(),
                            this.foodData.getFoodLevel(),
                            this.foodData.getSaturationLevel()));
        }
    }
}
