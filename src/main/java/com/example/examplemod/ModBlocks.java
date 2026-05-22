package com.example.examplemod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    // This registers the actual "Fake Light" block
    public static final RegistryObject<Block> FAKE_LIGHT = ExampleMod.BLOCKS.register("fake_light",
            () -> new AirBlock(BlockBehaviour.Properties.of() // Materials are no longer needed here
                    .setId(ExampleMod.BLOCKS.key("fake_light"))
                    .noCollision()
                    .noOcclusion()
                    .lightLevel((state) -> 15)
                    .replaceable()
                    .pushReaction(PushReaction.DESTROY) // Tells pistons to destroy it
            ));

    // This is the method we will call in your main class to finalize the registration
    public static void init() {
    }
}