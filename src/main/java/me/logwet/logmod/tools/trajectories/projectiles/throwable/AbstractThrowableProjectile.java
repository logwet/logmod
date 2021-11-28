package me.logwet.logmod.tools.trajectories.projectiles.throwable;

import java.util.ArrayList;
import java.util.List;
import me.logwet.logmod.mixin.common.trajectories.ProjectileInvoker;
import me.logwet.logmod.tools.trajectories.Trajectory;
import me.logwet.logmod.tools.trajectories.projectiles.IThrowableProjectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractThrowableProjectile implements IThrowableProjectile {
    @Override
    public Trajectory calculateTrajectory(Player parent) {
        List<Vec3> trajectoryList = new ArrayList<>();
        BlockHitResult blockHitResult = null;

        ThrowableProjectile projectileEntity = this.getBaseEntity(parent.level, parent);

        projectileEntity.shootFromRotation(
                parent,
                parent.xRot,
                parent.yRot,
                this.getVertScalingFac(),
                this.getVelScalingFac(),
                this.getRandScalingFac());

        trajectoryList.add(projectileEntity.position());

        int tickCount;
        for (tickCount = 0; tickCount <= 1200; tickCount++) {
            HitResult hitResult =
                    ProjectileUtil.getHitResult(
                            projectileEntity,
                            (entity) -> {
                                if (!entity.isSpectator()
                                        && entity.isAlive()
                                        && entity.isPickable()) {
                                    return parent.isPassengerOfSameVehicle(entity);
                                } else {
                                    return false;
                                }
                            },
                            ClipContext.Block.OUTLINE);

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockHitResult = (BlockHitResult) hitResult;
                break;
            }

            Vec3 vec3 = projectileEntity.getDeltaMovement();
            double d = projectileEntity.getX() + vec3.x;
            double e = projectileEntity.getY() + vec3.y;
            double f = projectileEntity.getZ() + vec3.z;
            ((ProjectileInvoker) projectileEntity).invokeUpdateRotation();

            Fluid fluid = parent.level.getFluidState(projectileEntity.blockPosition()).getType();

            boolean inWater = fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;

            projectileEntity.setDeltaMovement(
                    vec3.scale(inWater ? this.getWaterDrag() : this.getDrag()));

            Vec3 vec32 = projectileEntity.getDeltaMovement();
            projectileEntity.setDeltaMovement(
                    vec32.x, vec32.y - (double) this.getGravity(), vec32.z);

            projectileEntity.setPos(d, e, f);

            trajectoryList.add(projectileEntity.position());
        }

        projectileEntity.kill();

        return new Trajectory(trajectoryList, blockHitResult, Trajectory.RenderType.FILLED, 3);
    }
}
