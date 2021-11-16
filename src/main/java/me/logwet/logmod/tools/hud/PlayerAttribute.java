package me.logwet.logmod.tools.hud;

public class PlayerAttribute {
    private final float health;
    private final int foodLevel;
    private final float saturation;

    public PlayerAttribute(float health, int foodLevel, float saturation) {
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
    }

    public float getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }
}
