package com.example.examplemod.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo;
import net.minecraftforge.common.world.StructureModifier;

/**
 * Adds entries to a structure's spawn overrides (e.g. the ocean monument's guardian list).
 * Forge only ships a no-op structure modifier, so this fills the gap that
 * {@code forge:add_spawns} covers for biomes.
 */
public record AddStructureSpawnsModifier(HolderSet<Structure> structures, MobCategory category,
                                         WeightedList<MobSpawnSettings.SpawnerData> spawners) implements StructureModifier {

    public static final MapCodec<AddStructureSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(AddStructureSpawnsModifier::structures),
            MobCategory.CODEC.fieldOf("category").forGetter(AddStructureSpawnsModifier::category),
            WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawners").forGetter(AddStructureSpawnsModifier::spawners)
    ).apply(i, AddStructureSpawnsModifier::new));

    @Override
    public void modify(Holder<Structure> structure, Phase phase, StructureInfo.Builder builder) {
        if (phase != Phase.ADD || !this.structures.contains(structure)) return;

        var overrides = builder.getStructureSettings().getOrAddSpawnOverrides(this.category);
        for (Weighted<MobSpawnSettings.SpawnerData> spawner : this.spawners.unwrap()) {
            overrides.addSpawn(spawner.value(), spawner.weight());
        }
    }

    @Override
    public MapCodec<? extends StructureModifier> codec() {
        return CODEC;
    }
}
