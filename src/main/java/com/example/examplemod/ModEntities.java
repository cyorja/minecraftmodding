package com.example.examplemod;

import com.example.examplemod.entity.PiranhaEntity;
import com.example.examplemod.entity.BrineEntity;
import com.example.examplemod.entity.WaterBoltEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

    public static final RegistryObject<EntityType<BrineEntity>> BRINE = ENTITIES.register("brine",
            () -> EntityType.Builder.<BrineEntity>of(BrineEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(8)
                    .notInPeaceful()
                    .build(ENTITIES.key("brine")));

    public static final RegistryObject<EntityType<WaterBoltEntity>> WATER_BOLT = ENTITIES.register("water_bolt",
            () -> EntityType.Builder.<WaterBoltEntity>of(WaterBoltEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125f, 0.3125f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ENTITIES.key("water_bolt")));

    public static void init(BusGroup modBusGroup) {
        ENTITIES.register(modBusGroup);
    }
}
