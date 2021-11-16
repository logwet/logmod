package me.logwet.logmod.tools.overlay.trajectories;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class PlayerState {
    private final Vec3 pos;
    private final Vec2 rot;

    public PlayerState(Vec3 pos, Vec2 rot) {
        this.pos = pos;
        this.rot = rot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerState that = (PlayerState) o;
        return Objects.equals(pos, that.pos) && Objects.equals(rot, that.rot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, rot);
    }

    public Vec3 getPos() {
        return pos;
    }

    public Vec2 getRot() {
        return rot;
    }
}
