package me.logwet.logmod;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Level;

@Environment(EnvType.SERVER)
public class LogModServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LogMod.log(Level.INFO, "Server class initialized!");
    }
}
