package com.moyuans.blocklegend.rpgapi.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class RPGConfig {

    public static final ConfigClassHandler<RPGConfig> HANDLER = ConfigClassHandler.createBuilder(RPGConfig.class)
            .id(new ResourceLocation("blocklegend-rpg-api", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("blocklegend-rpg-api.json"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public boolean showChatMessages = false;

    @SerialEntry
    public boolean debugMode = false;
}