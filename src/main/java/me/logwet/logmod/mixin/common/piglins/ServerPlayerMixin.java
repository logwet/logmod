package me.logwet.logmod.mixin.common.piglins;

import static org.apache.logging.log4j.Level.ERROR;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    private static final String teamName = LogMod.MODID + "_tpt";
    @Shadow @Final public MinecraftServer server;

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
            scoreboard.removePlayerTeam(team);
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
    private void applyGlowingToList(List<Piglin> list) {
        for (Piglin piglin : list) {
            piglin.removeEffect(MobEffects.GLOWING);
            piglin.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, true, false));
        }
    }

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/player/Player;tick()V",
                            shift = Shift.BEFORE))
    private void onTick(CallbackInfo ci) {
        if (LogMod.IS_CLIENT && LogModData.isPiglinsEnabled()) {
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

            if (hitResult.getType() != Type.MISS) {}

            calculate:
            {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                    BlockPos blockpos = blockHitResult.getBlockPos();
                    BlockState blockState = this.level.getBlockState(blockpos);

                    if (blockState.getBlock().is(BlockTags.GUARDED_BY_PIGLINS)) {
                        PiglinAggroRange oldAggroRange = LogModData.getAggroRange(this.getUUID());

                        if (Objects.nonNull(oldAggroRange)) {
                            BlockPos oldBlockTarget = oldAggroRange.getBlockTarget();
                            if (Objects.nonNull(oldBlockTarget)) {
                                if (blockpos.asLong() != oldBlockTarget.asLong()) {
                                    emptyTeam(0);
                                    emptyTeam(1);
                                    emptyTeam(2);
                                }
                            }
                        }

                        initTeam(0, ChatFormatting.AQUA);
                        initTeam(1, ChatFormatting.RED);
                        initTeam(2, ChatFormatting.GOLD);

                        boolean bl = !blockState.getBlock().is(Blocks.GOLD_BLOCK);

                        List<Piglin> piglinList = new ArrayList<>();

                        AABB searchBB = this.getBoundingBox().inflate(16.0D);

                        List<Piglin> list = this.level.getEntitiesOfClass(Piglin.class, searchBB);
                        list.stream()
                                .filter((piglin) -> !bl || BehaviorUtils.canSee(piglin, this))
                                .forEach(
                                        (piglin) -> {
                                            if (piglin.getBrain().isActive(Activity.IDLE)) {
                                                addEntityToTeam(piglin, 1);
                                            } else {
                                                addEntityToTeam(piglin, 2);
                                            }
                                            piglinList.add(piglin);
                                        });

                        applyGlowingToList(piglinList);

                        LogModData.addAggroRange(
                                this.getUUID(), new PiglinAggroRange(blockpos, searchBB));
                        break calculate;
                    }
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                    List<Piglin> piglinList = new ArrayList<>();

                    Piglin piglin = (Piglin) entityHitResult.getEntity();

                    PiglinAggroRange oldAggroRange = LogModData.getAggroRange(this.getUUID());

                    if (Objects.nonNull(oldAggroRange)) {
                        Integer oldEntityTarget = oldAggroRange.getEntityTarget();
                        if (Objects.nonNull(oldEntityTarget)) {
                            if (piglin.getId() != oldEntityTarget) {
                                emptyTeam(0);
                                emptyTeam(1);
                                emptyTeam(2);
                            }
                        }
                    }

                    initTeam(0, ChatFormatting.AQUA);
                    initTeam(1, ChatFormatting.RED);
                    initTeam(2, ChatFormatting.GOLD);

                    addEntityToTeam(piglin, 0);
                    applyGlowingToList(Collections.singletonList(piglin));

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
                                        } else {
                                            addEntityToTeam(piglinx, 2);
                                        }

                                        piglinList.add(piglinx);
                                    });

                    applyGlowingToList(piglinList);

                    LogModData.addAggroRange(this.getUUID(), new PiglinAggroRange(piglin.getId()));

                    break calculate;
                }

                LogModData.removeAggroRange(this.getUUID());
            }
        }
    }
}
