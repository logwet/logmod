package me.logwet.logmod.tools.trajectories;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;

public class PearlProjectile implements IProjectile {
    public static PearlProjectile INSTANCE = new PearlProjectile();

    @Override
    public ThrowableProjectile getBaseProjectile(Level level, Player player) {
        return new ThrownEnderpearl(level, player);
    }
}
