package com.example.examplemod;

import com.example.examplemod.entity.PiranhaEntity;
import com.example.examplemod.entity.TidecallerEntity;
import com.example.examplemod.entity.WaterBoltEntity;
import com.example.examplemod.world.AddStructureSpawnsModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);

    public static final RegistryObject<EntityType<PiranhaEntity>> PIRANHA = ENTITIES.register("piranha",
            () -> EntityType.Builder.<PiranhaEntity>of(PiranhaEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.7f, 0.4f)
                    .clientTrackingRange(10)
                    .build(ENTITIES.key("piranha")));

    public static final RegistryObject<EntityType<TidecallerEntity>> TIDECALLER = ENTITIES.register("tidecaller",
            () -> EntityType.Builder.<TidecallerEntity>of(TidecallerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(8)
                    .notInPeaceful()
                    .build(ENTITIES.key("tidecaller")));

    public static final RegistryObject<EntityType<WaterBoltEntity>> WATER_BOLT = ENTITIES.register("water_bolt",
            () -> EntityType.Builder.<WaterBoltEntity>of(WaterBoltEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125f, 0.3125f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ENTITIES.key("water_bolt")));

    public static final DeferredRegister<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, ExampleMod.MODID);

    public static final RegistryObject<MapCodec<AddStructureSpawnsModifier>> ADD_STRUCTURE_SPAWNS =
            STRUCTURE_MODIFIER_SERIALIZERS.register("add_spawns", () -> AddStructureSpawnsModifier.CODEC);

    public static void init(BusGroup modBusGroup) {
        ENTITIES.register(modBusGroup);
        STRUCTURE_MODIFIER_SERIALIZERS.register(modBusGroup);
    }
}
