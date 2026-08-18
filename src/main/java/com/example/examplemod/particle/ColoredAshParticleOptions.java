package com.example.examplemod.particle;

import com.example.examplemod.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class ColoredAshParticleOptions implements ParticleOptions {

    public static final MapCodec<ColoredAshParticleOptions> CODEC =
            ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color")
                    .xmap(ColoredAshParticleOptions::new, o -> o.color);

    public static final StreamCodec<ByteBuf, ColoredAshParticleOptions> STREAM_CODEC =
            ByteBufCodecs.INT.map(ColoredAshParticleOptions::new, o -> o.color);

    private final int color;

    public ColoredAshParticleOptions(int color) {
        this.color = color;
    }

    @Override
    public ParticleType<ColoredAshParticleOptions> getType() {
        return ModParticles.COLORED_ASH.get();
    }

    public Vector3f getColor() {
        return new Vector3f(
                ARGB.red(this.color) / 255.0F,
                ARGB.green(this.color) / 255.0F,
                ARGB.blue(this.color) / 255.0F
        );
    }
}
