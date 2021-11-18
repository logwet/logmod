package me.logwet.logmod.mixin.common.trajectories;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker("checkInBlock")
    void checkInBlock(double d, double e, double f);
}
