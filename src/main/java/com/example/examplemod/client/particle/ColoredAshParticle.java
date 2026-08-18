package com.example.examplemod.client.particle;

import com.example.examplemod.particle.ColoredAshParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public class ColoredAshParticle extends BaseAshSmokeParticle {

    private ColoredAshParticle(ClientLevel level, double x, double y, double z,
                                double xa, double ya, double za,
                                Vector3f color, SpriteSet sprites) {
        super(level, x, y, z, 0.1F, -0.1F, 0.1F, xa, ya, za, 1.0F, sprites, 0.0F, 20, 0.0125F, false);
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
    }

    public static class Provider implements ParticleProvider<ColoredAshParticleOptions> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColoredAshParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xAux, double yAux, double zAux,
                                       RandomSource random) {
            double xa = random.nextFloat() * -1.9 * random.nextFloat() * 0.1;
            double ya = random.nextFloat() * -0.5 * random.nextFloat() * 0.1 * 5.0;
            double za = random.nextFloat() * -1.9 * random.nextFloat() * 0.1;
            return new ColoredAshParticle(level, x, y, z, xa, ya, za, options.getColor(), this.sprites);
        }
    }
}
