package me.logwet.logmod.tools.trajectories;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public interface IProjectile<T extends Entity> {
    Predicate<Item> getTriggerPredicate();

    T getBaseEntity(Level level, Player player);

    default float getGravity() {
        return 0.03F;
    }

    default float getDrag() {
        return 0.99F;
    }

    default float getWaterDrag() {
        return 0.8F;
    }

    default float getVertScalingFac() {
        return 0.0F;
    }

    default float getVelScalingFac() {
        return 1.5F;
    }

    default float getRandScalingFac() {
        return 1.0F;
    }
}
