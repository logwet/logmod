package me.logwet.logmod.tools.paths;

import java.util.EnumMap;
import java.util.Map;

public class PathSet {
    private final Map<ItemTrigger, PathContainer> pathMap = new EnumMap<>(ItemTrigger.class);

    public PathContainer getPath(ItemTrigger trigger) {
        return pathMap.get(trigger);
    }

    public void addPath(ItemTrigger trigger, PathContainer path) {
        pathMap.put(trigger, path);
    }

    public void removePath(ItemTrigger trigger) {
        pathMap.remove(trigger);
    }
}
