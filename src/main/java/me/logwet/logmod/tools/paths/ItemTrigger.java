package me.logwet.logmod.tools.paths;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ItemTrigger {
    WHITE(Items.WHITE_WOOL),
    ORANGE(Items.ORANGE_WOOL),
    MAGENTA(Items.MAGENTA_WOOL),
    LIGHT_BLUE(Items.LIGHT_BLUE_WOOL),
    YELLOW(Items.YELLOW_WOOL),
    LIME(Items.LIME_WOOL),
    PINK(Items.PINK_WOOL),
    GRAY(Items.GRAY_WOOL),
    LIGHT_GRAY(Items.LIGHT_GRAY_WOOL),
    CYAN(Items.CYAN_WOOL),
    PURPLE(Items.PURPLE_WOOL),
    BLUE(Items.BLUE_WOOL),
    BROWN(Items.BROWN_WOOL),
    GREEN(Items.GREEN_WOOL),
    RED(Items.RED_WOOL),
    BLACK(Items.BLACK_WOOL);

    private static final Map<Item, ItemTrigger> triggerMap;
    private static final Map<Block, ItemTrigger> triggerBlockMap;
    private static final Map<Material, ItemTrigger> materialMap;
    private static final Map<MaterialColor, ItemTrigger> colorMap;

    static {
        triggerMap = Maps.newHashMapWithExpectedSize(ItemTrigger.values().length);
        triggerBlockMap = new HashMap<>();
        materialMap = new HashMap<>();
        colorMap = new HashMap<>();

        for (ItemTrigger itemTrigger : ItemTrigger.values()) {
            triggerMap.put(itemTrigger.trigger, itemTrigger);

            if (itemTrigger.isBlock) {
                triggerBlockMap.putIfAbsent(itemTrigger.triggerBlock, itemTrigger);
                materialMap.putIfAbsent(itemTrigger.material, itemTrigger);
                colorMap.putIfAbsent(itemTrigger.color, itemTrigger);
            }
        }
    }

    @NotNull private final Item trigger;

    private final boolean isBlock;

    @Nullable private final Block triggerBlock;
    @Nullable private final Material material;
    @Nullable private final MaterialColor color;

    ItemTrigger(@NotNull Item trigger) {
        this.trigger = trigger;

        if (trigger instanceof BlockItem) {
            this.isBlock = true;

            this.triggerBlock = ((BlockItem) trigger).getBlock();
            this.material = this.triggerBlock.defaultBlockState().getMaterial();
            this.color = this.triggerBlock.defaultMaterialColor();
        } else {
            this.isBlock = false;

            this.triggerBlock = null;
            this.material = null;
            this.color = null;
        }
    }

    @Nullable
    public static ItemTrigger fromTrigger(BlockItem trigger) {
        return triggerMap.get(trigger);
    }

    @Nullable
    public static ItemTrigger fromTriggerBlock(Block triggerBlock) {
        return triggerBlockMap.get(triggerBlock);
    }

    @Nullable
    public static ItemTrigger fromMaterial(Material material) {
        return materialMap.get(material);
    }

    @Nullable
    public static ItemTrigger fromColor(MaterialColor color) {
        return colorMap.get(color);
    }

    public @NotNull Item getTrigger() {
        return trigger;
    }

    public @Nullable Block getTriggerBlock() {
        return triggerBlock;
    }

    public @Nullable Material getMaterial() {
        return material;
    }

    public @Nullable MaterialColor getColor() {
        return color;
    }

    public @Nullable Float[] getRGB() {
        if (!Objects.isNull(color)) {
            int j = color.col;

            float r = (float) (j >> 16 & 255) / 255.0F;
            float g = (float) (j >> 8 & 255) / 255.0F;
            float b = (float) (j & 255) / 255.0F;

            return new Float[] {r, g, b};
        } else {
            return null;
        }
    }
}
