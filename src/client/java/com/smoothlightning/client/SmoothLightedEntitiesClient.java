package com.smoothlightning.client;

import com.smoothlightning.client.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class SmoothLightedEntitiesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModConfig.checkAndReload();
        });
    }
}