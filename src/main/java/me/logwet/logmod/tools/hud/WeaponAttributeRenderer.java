package me.logwet.logmod.tools.hud;

import fi.dy.masa.malilib.config.HudAlignment;
import me.logwet.logmod.LogMod;
import me.logwet.logmod.LogModData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class WeaponAttributeRenderer extends AbstractTextRenderer {
    public WeaponAttributeRenderer() {
        super(HudAlignment.BOTTOM_RIGHT);
    }

    @Override
    public void update(Minecraft MC) {
        this.lines.clear();

        if (LogMod.isInSingleplayer()) {
            Entity entity = MC.getCameraEntity();
            assert entity != null;

            if (entity instanceof Player) {
                WeaponAttribute weaponAttribute;
                if ((weaponAttribute = LogModData.getWeaponAttribute(entity.getUUID())) != null) {
                    boolean isCharged = weaponAttribute.isCharged();
                    boolean isFalling = weaponAttribute.isFalling();
                    boolean isCrit = weaponAttribute.isCrit();

                    this.lines.add(isCrit ? "Critical hit!" : "Normal hit");

                    this.lines.add(
                            String.format("Damage: %.3f", weaponAttribute.getAttackDamage()));
                    this.lines.add(
                            String.format("Charge: %.2f", weaponAttribute.getChargePercent() * 100F)
                                    + "%");

                    this.lines.add(
                            (isCharged ? "Charged" : "Not-charged")
                                    + " | "
                                    + (isFalling ? "Falling" : "Not-falling"));
                }
            }
        }
    }
}
