package com.example.examplemod;

import com.example.examplemod.entity.PiranhaEntity;
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

    public static void init(BusGroup modBusGroup) {
        ENTITIES.register(modBusGroup);
    }
}
