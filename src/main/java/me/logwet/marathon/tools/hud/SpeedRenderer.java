package me.logwet.marathon.tools.hud;

import fi.dy.masa.malilib.config.HudAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class SpeedRenderer extends AbstractTextRenderer {
    public SpeedRenderer() {
        super(HudAlignment.TOP_LEFT);
    }

    @Override
    public void update(Minecraft MC) {
        this.lines.clear();

        Entity entity = MC.getCameraEntity();

        assert entity != null;

        double dx = entity.getX() - entity.xOld;
        double dy = entity.getY() - entity.yOld;
        double dz = entity.getZ() - entity.zOld;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        this.addLine(String.format("Absolute Speed: %.3f m/s", dist * 20));
        this.addLine(
                String.format(
                        "Axis Speed: x: %.3f y: %.3f z: %.3f m/s", dx * 20, dy * 20, dz * 20));
    }
}
