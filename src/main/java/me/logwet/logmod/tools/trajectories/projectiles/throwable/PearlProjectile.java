package me.logwet.logmod.tools.trajectories.projectiles.throwable;

import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PearlProjectile extends AbstractThrowableProjectile {
    public static PearlProjectile INSTANCE = new PearlProjectile();

    @Override
    public Predicate<Item> getTriggerPredicate() {
        return (item) -> item == Items.ENDER_PEARL;
    }

    @Override
    public ThrowableProjectile getBaseEntity(Level level, Player player) {
        return new ThrownEnderpearl(level, player);
    }
}
