package me.logwet.logmod.tools.trajectories.projectiles.dropped;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class GoldIngotProjectile extends AbstractDroppedItemProjectile {
    public static GoldIngotProjectile INSTANCE = new GoldIngotProjectile();
    protected static Item itemType = Items.GOLD_INGOT;

    @Override
    public Predicate<Item> getTriggerPredicate() {
        return (item) -> item == itemType;
    }

    @Override
    public ItemEntity getBaseEntity(Level level, Player player) {
        return super.getBaseEntity(level, player, itemType);
    }
}
