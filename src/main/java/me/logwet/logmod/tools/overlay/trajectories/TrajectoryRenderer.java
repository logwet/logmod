package me.logwet.logmod.tools.overlay.trajectories;

import com.mojang.blaze3d.vertex.PoseStack;
import me.logwet.logmod.LogModData;
import me.logwet.logmod.tools.overlay.RenderOverlay;
import net.minecraft.client.Minecraft;

public class TrajectoryRenderer implements RenderOverlay {
    private long infoUpdateTime;

    @Override
    public void update(Minecraft MC) {
        assert MC.player != null;

        Trajectory trajectory;
        if ((trajectory = LogModData.getTrajectory(MC.player.getUUID())) != null) {
            System.out.println(trajectory.getTrajectory());
        }
    }

    @Override
    public void onPostRenderGameOverlay(Minecraft MC, PoseStack poseStack, float partialTicks) {
        if (MC.player != null && !MC.options.hideGui) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - this.infoUpdateTime >= 50) {
                this.update(MC);
                this.infoUpdateTime = currentTime;
            }
        }
    }
}
