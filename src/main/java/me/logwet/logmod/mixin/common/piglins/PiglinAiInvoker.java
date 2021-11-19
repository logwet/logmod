package me.logwet.logmod.mixin.common.piglins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;

@Mixin(PiglinAi.class)
public interface PiglinAiInvoker {
    @Invoker("isIdle")
    static boolean isIdle(Piglin piglin) {
        throw new AssertionError();
    }

    @Invoker("getAdultPiglins")
    static List<Piglin> getAdultPiglins(Piglin piglin) {
        throw new AssertionError();
    }

    @Invoker("getAngerTarget")
    static Optional<LivingEntity> getAngerTarget(Piglin piglin) {
        throw new AssertionError();
    }
}
