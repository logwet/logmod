package me.logwet.marathon.mixin.server;

import me.logwet.marathon.Marathon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.SERVER)
@Mixin(DedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {
    @Inject(method = "initServer", at = @At("HEAD"))
    private void injectSetupServer(CallbackInfoReturnable<Boolean> cir) {
        Marathon.log(Level.INFO, "This line is printed by a MinecraftDedicatedServer mixin!");
    }
}
