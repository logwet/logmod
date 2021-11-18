package me.logwet.logmod.tools.trajectories;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public interface IProjectile<T extends Entity> {
    Predicate<Item> getTriggerPredicate();

    T getBaseEntity(Level level, Player player);

    Trajectory calculateTrajectory(Player parentEntity);
}
