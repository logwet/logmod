package me.logwet.marathon.mixin.client;

import me.logwet.marathon.Marathon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Inject(method = "initServer", at = @At("HEAD"))
    private void onInitServer(CallbackInfoReturnable<Boolean> cir) {
        Marathon.onServerInit((MinecraftServer) (Object) this);
    }
}
