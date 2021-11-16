package me.logwet.logmod.mixin.server;

import me.logwet.logmod.LogModData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.SERVER)
@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin {
    @Inject(method = "initServer", at = @At("HEAD"))
    private void onInitServer(CallbackInfoReturnable<Boolean> cir) {
        LogModData.onServerInit((MinecraftServer) (Object) this);
    }
}
