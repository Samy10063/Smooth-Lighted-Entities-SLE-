package com.smoothlightning;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class SmoothLightedEntities implements ModInitializer {
	public static final String MOD_ID = "smooth-lighted-entities";


	@Override
	public void onInitialize() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
