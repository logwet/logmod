package me.logwet.logmod.tools.hud;

import fi.dy.masa.malilib.config.HudAlignment;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class AttributeRenderer extends AbstractTextRenderer {
    public AttributeRenderer() {
        super(HudAlignment.TOP_RIGHT);
    }

    @Override
    public void update(Minecraft MC) {
        this.lines.clear();

        if (LogMod.isInSingleplayer()) {
            Entity entity = MC.getCameraEntity();
            assert entity != null;

            if (entity instanceof Player) {
                PlayerAttribute playerAttribute;
                if ((playerAttribute = LogModData.getPlayerAttribute(entity.getUUID())) != null) {
                    this.lines.add(String.format("Health: %.3f", playerAttribute.getHealth()));
                    this.lines.add("Food: " + playerAttribute.getFoodLevel());
                    this.lines.add(
                            String.format("Saturation: %.3f", playerAttribute.getSaturation()));
                }
            }
        }
    }
}
