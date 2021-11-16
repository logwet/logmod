package me.logwet.logmod.mixin.common.trajectories;

import com.mojang.authlib.GameProfile;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.overlay.trajectories.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    private static final List<Item> targetItems =
            Arrays.asList(Items.ENDER_PEARL, Items.SPLASH_POTION);
    private final Map<UUID, Integer> playerStateHashes = new HashMap<>();

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
        if (LogMod.IS_CLIENT) {
            PlayerState playerState = new PlayerState(this.position(), this.getRotationVector());
            int currStateHash = playerState.hashCode();
            Integer lastStateHash = playerStateHashes.get(this.getUUID());

            if (lastStateHash == null) {
                playerStateHashes.put(this.getUUID(), currStateHash);
                lastStateHash = Integer.MAX_VALUE;
            }

            if (!lastStateHash.equals(currStateHash)) {
                Item holdingItem = null;

                for (Item targetItem : targetItems) {
                    if (this.isHolding(targetItem)) {
                        holdingItem = targetItem;
                        break;
                    }
                }

                if (holdingItem != null) {
                    IProjectile projectile = null;
                    ThrowableProjectile baseProjectile = null;

                    List<Vec3> trajectory = new ArrayList<>();
                    BlockHitResult blockHitResult = null;

                    if (Items.ENDER_PEARL.equals(holdingItem)) {
                        projectile = PearlProjectile.INSTANCE;
                    } else if (Items.SPLASH_POTION.equals(holdingItem)) {
                        projectile = PotionProjectile.INSTANCE;
                    }

                    assert projectile != null;
                    baseProjectile = projectile.getBaseProjectile(this.getLevel(), this);
                    baseProjectile.shootFromRotation(
                            this,
                            this.xRot,
                            this.yRot,
                            projectile.getVertScalingFac(),
                            projectile.getVelScalingFac(),
                            projectile.getRandScalingFac());

                    trajectory.add(baseProjectile.position());

                    int tickCount;
                    for (tickCount = 0; tickCount <= 1200; tickCount++) {
                        HitResult hitResult =
                                ProjectileUtil.getHitResult(
                                        baseProjectile,
                                        (entity) -> {
                                            if (!entity.isSpectator()
                                                    && entity.isAlive()
                                                    && entity.isPickable()) {
                                                return this.isPassengerOfSameVehicle(entity);
                                            } else {
                                                return false;
                                            }
                                        },
                                        ClipContext.Block.OUTLINE);

                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            blockHitResult = (BlockHitResult) hitResult;
                            break;
                        }

                        Vec3 vec3 = baseProjectile.getDeltaMovement();
                        double d = baseProjectile.getX() + vec3.x;
                        double e = baseProjectile.getY() + vec3.y;
                        double f = baseProjectile.getZ() + vec3.z;
                        ((ProjectileInvoker) baseProjectile).invokeUpdateRotation();

                        baseProjectile.setDeltaMovement(vec3.scale(projectile.getDrag()));

                        Vec3 vec32 = baseProjectile.getDeltaMovement();
                        baseProjectile.setDeltaMovement(
                                vec32.x, vec32.y - (double) projectile.getGravity(), vec32.z);

                        baseProjectile.setPos(d, e, f);

                        trajectory.add(baseProjectile.position());
                    }

                    baseProjectile.kill();

                    LogModData.addTrajectory(
                            this.getUUID(),
                            new Trajectory(
                                    playerState.getPos(),
                                    playerState.getRot(),
                                    trajectory,
                                    blockHitResult));
                } else {
                    LogModData.removeTrajectory(this.getUUID());
                }
            }
        }
    }
}
