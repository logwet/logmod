package me.logwet.logmod.tools.trajectories;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class PotionProjectile extends AbstractThrowableProjectile {
    public static PotionProjectile INSTANCE = new PotionProjectile();

    @Override
    public Predicate<Item> getTriggerPredicate() {
        return (item) -> item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
    }

    @Override
    public ThrowableProjectile getBaseEntity(Level level, Player player) {
        return new ThrownPotion(level, player);
    }

    @Override
    public float getGravity() {
        return 0.05F;
    }

    @Override
    public float getVertScalingFac() {
        return -20.0F;
    }

    @Override
    public float getVelScalingFac() {
        return 0.5F;
    }
}
