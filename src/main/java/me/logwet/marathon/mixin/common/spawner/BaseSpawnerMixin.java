package me.logwet.marathon.mixin.common.spawner;

import me.logwet.marathon.Marathon;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {
    @Shadow private SpawnData nextSpawnData;
    @Shadow private int spawnRange;
    @Shadow private int requiredPlayerRange;
    @Shadow private int spawnDelay;
    @Unique private boolean doneCalculations = true;
    @Shadow private int spawnCount;

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract BlockPos getPos();

    private double transformPosToProb(double pos, double range) {
        return (range - Math.abs(-pos)) / (range * range);
    }

    private double bivariatePosProb(double x, double z, double range) {
        return transformPosToProb(x, range) * transformPosToProb(z, range);
    }

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/level/BaseSpawner;spawnDelay:I",
                            opcode = Opcodes.PUTFIELD,
                            shift = At.Shift.AFTER))
    private void onDeincrementSpawnDelay(CallbackInfo ci) {
        if (!this.getLevel().isClientSide) {
            if (this.spawnDelay == 0) {
                this.doneCalculations = false;
            }
        }
    }

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/nbt/CompoundTag;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/ListTag;"))
    private void onSpawnAttemptStart(CallbackInfo ci) {
        if (this.doneCalculations) {
            return;
        }

        CompoundTag spawnerTag = this.nextSpawnData.getTag();

        Optional<EntityType<?>> entityOptional = EntityType.by(spawnerTag);
        if (!entityOptional.isPresent()) {
            return;
        }
        EntityType<?> entityType = entityOptional.get();

        Level level = this.getLevel();
        BlockPos blockPos = this.getPos();

        int bound = this.spawnRange * 16;

        double success = 0;
        double maxSize = 0;

        for (int x0 = -bound; x0 <= bound; x0++) {
            double x1 = ((double) x0 / (double) bound) * (double) this.spawnRange;
            double x = (double) blockPos.getX() + x1 + 0.5D;

            for (int z0 = -bound; z0 <= bound; z0++) {
                double z1 = ((double) z0 / (double) bound) * (double) this.spawnRange;
                double z = (double) blockPos.getZ() + z1 + 0.5D;

                double prob = bivariatePosProb(x1, z1, this.spawnRange);

                for (int y0 = -1; y0 <= 1; y0++) {
                    double y = (double) blockPos.getY() + y0;

                    maxSize += prob;

                    if (level.noCollision(entityType.getAABB(x, y, z))
                            && SpawnPlacements.checkSpawnRules(
                                    entityType,
                                    level.getLevel(),
                                    MobSpawnType.SPAWNER,
                                    new BlockPos(x, y, z),
                                    level.getRandom())) {
                        success += prob;
                    }
                }
            }
        }

        double percentage = success / maxSize;

        Marathon.log(
                org.apache.logging.log4j.Level.INFO,
                "Spawner: " + success + " / " + maxSize + " = " + percentage * 100D);

        Player player =
                this.getLevel()
                        .getNearestPlayer(
                                (new TargetingConditions()).range(this.requiredPlayerRange),
                                blockPos.getX(),
                                blockPos.getY(),
                                blockPos.getZ());

        StringBuilder messageString = new StringBuilder();

        for (int i = 1; i <= this.spawnCount; i++) {
            messageString
                    .append(i)
                    .append(": ")
                    .append(String.format("%.2f", Math.pow(percentage, i) * 100D))
                    .append("% ");
        }

        if (player != null) {
            if (player.isAlive()) {
                Component text =
                        new TextComponent(
                                        StringUtils.capitalize(
                                                        Registry.ENTITY_TYPE
                                                                .getKey(entityType)
                                                                .getPath())
                                                + " spawner at "
                                                + blockPos.toShortString()
                                                + " chance to spawn # of mobs: "
                                                + messageString)
                                .withStyle(ChatFormatting.GREEN);
                player.sendMessage(text, Util.NIL_UUID);
            }
        }

        this.doneCalculations = true;
    }
}
