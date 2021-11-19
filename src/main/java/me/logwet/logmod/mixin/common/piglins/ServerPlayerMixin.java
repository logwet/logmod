package me.logwet.logmod.mixin.common.piglins;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.piglins.PiglinAggroRange;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.Level.ERROR;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    private static final String teamName = LogMod.MODID + "_tpt";
    @Shadow @Final public MinecraftServer server;
    private long infoUpdateTime;

    public ServerPlayerMixin(Level level, BlockPos blockPos, GameProfile gameProfile) {
        super(level, blockPos, gameProfile);
    }

    @Unique
    private void initTeam(int index, ChatFormatting color) {
        Scoreboard scoreboard = this.server.getScoreboard();

        PlayerTeam team;

        if ((team = scoreboard.getPlayerTeam(teamName + "_" + index)) == null) {
            team = scoreboard.addPlayerTeam(teamName + "_" + index);
            team.setColor(color);
        }
    }

    @Unique
    private void emptyTeam(int index) {
        Scoreboard scoreboard = this.server.getScoreboard();

        PlayerTeam team = scoreboard.getPlayerTeam(teamName + "_" + index);

        if (team != null) {
            for (String entity : ImmutableList.copyOf(team.getPlayers())) {
                scoreboard.removePlayerFromTeam(entity, team);
            }
        }
    }

    @Unique
    private void addEntityToTeam(Entity entity, int index) {
        Scoreboard scoreboard = this.server.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName + "_" + index);

        if (team != null) {
            scoreboard.addPlayerToTeam(entity.getStringUUID(), team);
        } else {
            LogMod.log(ERROR, "Unable to get team for index " + index);
        }
    }

    @Unique
    private void applyGlowingToList(List<Integer> list) {
        for (Integer id : list) {
            Piglin piglin = (Piglin) this.level.getEntity(id);
            assert piglin != null;
            piglin.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
        }
    }

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/player/Player;tick()V",
                            shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (LogMod.IS_CLIENT && LogModData.isPiglinsEnabled()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.infoUpdateTime >= 200) {
                this.infoUpdateTime = currentTime;

                HitResult hitResult;
                {
                    double d = 32.0D;

                    hitResult = this.pick(d, 1.0F, true);

                    Vec3 vec3 = this.position();
                    Vec3 vec32 = this.getViewVector(1.0F);
                    Vec3 vec33 = vec3.add(vec32.x * d, vec32.y * d, vec32.z * d);

                    AABB aABB =
                            this.getBoundingBox()
                                    .expandTowards(vec32.scale(d))
                                    .inflate(1.0D, 1.0D, 1.0D);
                    EntityHitResult entityHitResult =
                            ProjectileUtil.getEntityHitResult(
                                    this,
                                    vec3,
                                    vec33,
                                    aABB,
                                    (entityx) -> !entityx.isSpectator() && entityx.isPickable(),
                                    d * d);

                    if (entityHitResult != null) {
                        if (entityHitResult.getEntity().getType() == EntityType.PIGLIN) {
                            hitResult = entityHitResult;
                        }
                    }
                }
                calculate:
                {
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                        BlockPos blockpos = blockHitResult.getBlockPos();
                        BlockState blockState = this.level.getBlockState(blockpos);

                        if (blockState.getBlock().is(BlockTags.GUARDED_BY_PIGLINS)) {
                            boolean bl = !blockState.getBlock().is(Blocks.GOLD_BLOCK);

                            List<Integer> piglinList = new ArrayList<>();

                            emptyTeam(1);

                            initTeam(1, ChatFormatting.GREEN);

                            AABB searchBB = this.getBoundingBox().inflate(16.0D);

                            List<Piglin> list =
                                    this.level.getEntitiesOfClass(Piglin.class, searchBB);
                            list.stream()
                                    .filter(piglin -> piglin.getBrain().isActive(Activity.IDLE))
                                    .filter((piglin) -> !bl || BehaviorUtils.canSee(piglin, this))
                                    .forEach(
                                            (piglin) -> {
                                                addEntityToTeam(piglin, 1);
                                                piglinList.add(piglin.getId());
                                            });

                            applyGlowingToList(piglinList);

                            LogModData.addAggroRange(
                                    this.getUUID(),
                                    new PiglinAggroRange(blockpos, searchBB, piglinList));
                            break calculate;
                        }
                    } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                        List<Integer> piglinList = new ArrayList<>();

                        Piglin piglin = (Piglin) entityHitResult.getEntity();

                        emptyTeam(0);
                        emptyTeam(1);

                        initTeam(0, ChatFormatting.RED);
                        initTeam(1, ChatFormatting.AQUA);

                        addEntityToTeam(piglin, 0);
                        applyGlowingToList(Collections.singletonList(piglin.getId()));

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
                                                addEntityToTeam(piglinx, 1);
                                                piglinList.add(piglinx.getId());
                                            }
                                        });

                        applyGlowingToList(piglinList);

                        LogModData.addAggroRange(
                                this.getUUID(), new PiglinAggroRange(piglin.getId(), piglinList));

                        break calculate;
                    }

                    LogModData.removeAggroRange(this.getUUID());
                }
            }
        }
    }
}
