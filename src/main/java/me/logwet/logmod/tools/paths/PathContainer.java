package me.logwet.logmod.tools.paths;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PathContainer {
    private final ItemTrigger itemTrigger;
    private final List<Vec3> nodes;

    public PathContainer(ItemTrigger itemTrigger) {
        this.itemTrigger = itemTrigger;
        this.nodes = new ArrayList<>();
    }

    public List<Vec3> getNodes() {
        return nodes;
    }

    public ItemTrigger getItemTrigger() {
        return itemTrigger;
    }

    public void addNode(Vec3 node) {
        nodes.add(node);
    }

    public void addNode(BlockPos blockPos) {
        addNode(Vec3.atCenterOf(blockPos));
    }
}
