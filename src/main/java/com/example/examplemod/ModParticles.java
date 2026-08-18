package com.example.examplemod;

import com.example.examplemod.particle.ColoredAshParticleOptions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ExampleMod.MODID);

    public static final RegistryObject<ParticleType<ColoredAshParticleOptions>> COLORED_ASH =
            PARTICLE_TYPES.register("colored_ash", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<ColoredAshParticleOptions> codec() {
                    return ColoredAshParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, ColoredAshParticleOptions> streamCodec() {
                    return ColoredAshParticleOptions.STREAM_CODEC;
                }
            });

    public static void init(BusGroup modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
