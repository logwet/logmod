package me.logwet.logmod.mixin.common.trajectories;

import com.mojang.authlib.GameProfile;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.trajectories.projectiles.IProjectile;
import me.logwet.logmod.tools.trajectories.projectiles.throwable.PearlProjectile;
import me.logwet.logmod.tools.trajectories.projectiles.throwable.PotionProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @SuppressWarnings("rawtypes")
    private static final List<IProjectile> trackedProjectiles =
            Arrays.asList(PearlProjectile.INSTANCE, PotionProjectile.INSTANCE);

    public ServerPlayerMixin(Level level, BlockPos blockPos, GameProfile gameProfile) {
        super(level, blockPos, gameProfile);
    }

    @Shadow
    public abstract ServerLevel getLevel();

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/player/Player;tick()V",
                            shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (LogMod.IS_CLIENT && LogModData.isProjectilesEnabled()) {
            @SuppressWarnings("rawtypes")
            IProjectile projectile = null;

            for (@SuppressWarnings("rawtypes") IProjectile targetProjectile : trackedProjectiles) {
                @SuppressWarnings("unchecked")
                Predicate<Item> predicate = targetProjectile.getTriggerPredicate();

                if (this.isHolding(predicate)) {
                    projectile = targetProjectile;
                    break;
                }
            }

            if (projectile != null) {
                LogModData.addTrajectory(this.getUUID(), projectile.calculateTrajectory(this));
            } else {
                LogModData.removeTrajectory(this.getUUID());
            }
        }
    }
}
