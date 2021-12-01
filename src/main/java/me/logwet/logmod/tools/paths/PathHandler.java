package me.logwet.logmod.tools.paths;

import me.logwet.logmod.LogModData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

@Environment(EnvType.CLIENT)
public class PathHandler {
    public static void buildPath(Minecraft client) {
        Player player = client.player;
        assert player != null;

        double d = 64.0D;
        HitResult hitResult = player.pick(d, 1.0F, true);

        if (hitResult.getType() == Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

            Item item = player.getMainHandItem().getItem();

            if (item instanceof BlockItem) {
                ItemTrigger trigger = ItemTrigger.fromTrigger((BlockItem) item);

                if (trigger != null) {
                    BlockPos blockpos =
                            blockHitResult.getBlockPos().relative(blockHitResult.getDirection());

                    if (client.getSingleplayerServer() != null) {
                        long seed =
                                client.getSingleplayerServer()
                                        .getWorldData()
                                        .worldGenSettings()
                                        .seed();

                        PathSet pathSet = LogModData.getPathSet(seed);
                        if (pathSet == null) {
                            pathSet = new PathSet();
                            LogModData.addPathSet(seed, pathSet);
                        }

                        PathContainer pathContainer = pathSet.getPath(trigger);
                        if (pathContainer == null) {
                            pathContainer = new PathContainer(trigger);
                            pathSet.addPath(trigger, pathContainer);
                        }

                        pathContainer.addNode(blockpos);
                    }
                }
            }
        }
    }

    public static void deletePath(Minecraft client) {
        Player player = client.player;
        assert player != null;

        Item item = player.getMainHandItem().getItem();

        if (item instanceof BlockItem) {
            ItemTrigger trigger = ItemTrigger.fromTrigger((BlockItem) item);

            if (trigger != null) {
                if (client.getSingleplayerServer() != null) {
                    long seed =
                            client.getSingleplayerServer().getWorldData().worldGenSettings().seed();

                    PathSet pathSet = LogModData.getPathSet(seed);
                    if (pathSet == null) {
                        pathSet = new PathSet();
                        LogModData.addPathSet(seed, pathSet);
                    }

                    pathSet.removePath(trigger);
                }
            }
        }
    }
}
