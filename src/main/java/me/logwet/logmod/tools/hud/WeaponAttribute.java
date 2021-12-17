package me.logwet.logmod.tools.hud;

public class WeaponAttribute {
    private final float attackDamage;
    private final float chargePercent;
    private final boolean isCharged;
    private final boolean isFalling;
    private final boolean isCrit;

    public WeaponAttribute(
            float attackDamage,
            float chargePercent,
            boolean isCharged,
            boolean isFalling,
            boolean isCrit) {
        this.attackDamage = attackDamage;
        this.chargePercent = chargePercent;
        this.isCharged = isCharged;
        this.isFalling = isFalling;
        this.isCrit = isCrit;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getChargePercent() {
        return chargePercent;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public boolean isCrit() {
        return isCrit;
    }
}
