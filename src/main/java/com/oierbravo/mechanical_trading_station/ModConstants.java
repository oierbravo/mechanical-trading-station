package com.oierbravo.mechanical_trading_station;

import net.minecraft.resources.ResourceLocation;

public class ModConstants {
    public static final String MODID = "mechanical_trading_station";
    public static final String DISPLAY_NAME = "Mechanical Trading Station";

    public static ResourceLocation asResource(String path) {
        return asResource().withPath(path);
    }

    public static ResourceLocation asResource() {
        return ResourceLocation.fromNamespaceAndPath(MODID,"");
    }
}
