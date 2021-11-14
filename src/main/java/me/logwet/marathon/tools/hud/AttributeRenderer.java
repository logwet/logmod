package me.logwet.marathon.tools.hud;

import fi.dy.masa.malilib.config.HudAlignment;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.MarathonData;
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

        if (Marathon.isInSingleplayer()) {
            Entity entity = MC.getCameraEntity();
            assert entity != null;

            if (entity instanceof Player) {
                PlayerAttribute playerAttribute;
                if ((playerAttribute = MarathonData.getPlayerAttribute(entity.getUUID())) != null) {
                    this.lines.add(String.format("Health: %.3f", playerAttribute.getHealth()));
                    this.lines.add("Food: " + playerAttribute.getFoodLevel());
                    this.lines.add(
                            String.format("Saturation: %.3f", playerAttribute.getSaturation()));
                }
            }
        }
    }
}
