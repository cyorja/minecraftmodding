package com.example.examplemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class ModBiomes {
    public static final ResourceKey<Biome> CRIMSON_PLAINS = ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(ExampleMod.MODID, "crimson_plains")
    );
}
