package me.logwet.marathon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Level;

@Environment(EnvType.CLIENT)
public class MarathonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Marathon.log(Level.INFO, "Client class initialized!");
    }
}
