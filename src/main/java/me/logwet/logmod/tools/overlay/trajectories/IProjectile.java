package me.logwet.logmod.tools.overlay.trajectories;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IProjectile {
    ThrowableProjectile getBaseProjectile(Level level, Player player);

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

    default Vec3 getInitialVelocity(Entity entity) {
        float f = entity.xRot;
        float g = entity.yRot;

        float h = getVertScalingFac();
        float i = getVelScalingFac();
        float j = getRandScalingFac();

        float k = -Mth.sin(g * 0.017453292F) * Mth.cos(f * 0.017453292F);
        float l = -Mth.sin((f + h) * 0.017453292F);
        float m = Mth.cos(g * 0.017453292F) * Mth.cos(f * 0.017453292F);

        Vec3 projVel = (new Vec3(k, l, m)).normalize().scale(i);

        Vec3 entityVel = entity.getDeltaMovement();
        Vec3 baseVel = new Vec3(entityVel.x, entity.isOnGround() ? 0.0D : entityVel.y, entityVel.z);

        return projVel.add(baseVel);
    }
}
