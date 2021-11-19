package me.logwet.logmod.mixin.common.piglins;

import com.mojang.authlib.GameProfile;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.piglins.PiglinAggroRange;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    private long infoUpdateTime;

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
        if (LogMod.IS_CLIENT) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.infoUpdateTime >= 200) {
                this.infoUpdateTime = currentTime;

                HitResult hitResult = this.pick(32.0D, 1.0F, false);

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                    BlockPos blockpos = blockHitResult.getBlockPos();
                    BlockState blockState = this.level.getBlockState(blockpos);

                    if (blockState.getBlock().is(BlockTags.GUARDED_BY_PIGLINS)) {
                        boolean bl = !blockState.getBlock().is(Blocks.GOLD_BLOCK);

                        List<Integer> piglinList = new ArrayList<>();

                        AABB searchBB = this.getBoundingBox().inflate(16.0D);

                        List<Piglin> list = this.level.getEntitiesOfClass(Piglin.class, searchBB);
                        list.stream()
                                .filter(PiglinAiInvoker::isIdle)
                                .filter((piglin) -> !bl || BehaviorUtils.canSee(piglin, this))
                                .forEach((piglin) -> piglinList.add(piglin.getId()));

                        LogModData.addAggroRange(
                                this.getUUID(),
                                new PiglinAggroRange(blockpos, searchBB, piglinList));
                    }

                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                    Entity entity = entityHitResult.getEntity();

                    if (entity.getType() == EntityType.PIGLIN) {
                        List<Integer> piglinList = new ArrayList<>();

                        Piglin piglin = (Piglin) entity;

                        PiglinAiInvoker.getAdultPiglins(piglin)
                                .forEach(
                                        (piglinx) -> {
                                            Optional<LivingEntity> optional =
                                                    PiglinAiInvoker.getAngerTarget(piglinx);

                                            LivingEntity livingEntity2 =
                                                    BehaviorUtils.getNearestTarget(
                                                            piglinx, optional, this);

                                            if (!optional.isPresent()
                                                    || optional.get() != livingEntity2) {
                                                piglinList.add(piglinx.getId());
                                            }
                                        });

                        LogModData.addAggroRange(
                                this.getUUID(),
                                new PiglinAggroRange(piglin.getId(), null, piglinList));
                    }
                } else {
                    LogModData.removeAggroRange(this.getUUID());
                }
            }
        }
    }
}
