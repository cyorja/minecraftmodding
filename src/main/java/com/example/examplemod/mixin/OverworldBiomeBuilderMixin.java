package com.example.examplemod.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public class OverworldBiomeBuilderMixin {

    private static final ResourceKey<Biome> CRIMSON_PLAINS = ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath("examplemod", "crimson_plains")
    );

    @SuppressWarnings("unchecked")
    @Inject(method = "addBiomes(Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void addCrimsonPlains(Consumer biomes, CallbackInfo ci) {
        // Injects crimson plains at plains-adjacent climate parameters:
        // warm temperature, dry humidity, mid-to-far inland, low erosion, slightly weird
        biomes.accept(Pair.of(
                Climate.parameters(
                        Climate.Parameter.span(-0.45F, 0.2F),    // temperature: cool to warm (plains range)
                        Climate.Parameter.span(-1.0F, -0.35F),   // humidity: dry (plains range)
                        Climate.Parameter.span(-0.11F, 1.0F),    // continentalness: near-inland to far
                        Climate.Parameter.span(-0.78F, 0.45F),   // erosion: low to medium
                        Climate.Parameter.point(0.0F),            // depth: surface only
                        Climate.Parameter.span(-0.56F, 0.0F),    // weirdness: non-weird half (where plains appears)
                        0.0F                                      // offset
                ),
                CRIMSON_PLAINS
        ));
    }
}
