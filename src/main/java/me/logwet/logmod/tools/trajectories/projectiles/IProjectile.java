package me.logwet.logmod.tools.trajectories.projectiles;

import java.util.function.Predicate;
import me.logwet.logmod.tools.trajectories.Trajectory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public interface IProjectile<T extends Entity> {
    Predicate<Item> getTriggerPredicate();

    T getBaseEntity(Level level, Player player);

    Trajectory calculateTrajectory(Player parent);
}
