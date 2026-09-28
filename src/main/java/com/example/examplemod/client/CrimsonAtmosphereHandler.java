package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBiomes;
import com.example.examplemod.particle.ColoredAshParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Drives Crimson Plains' mist thickness and ash density from time of day.
// Both use the same day/night curve, plateaued through the day and night and
// only transitioning around dawn/dusk - mirroring how vanilla sky light behaves.
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CrimsonAtmosphereHandler {

    private static final float NIGHT_FOG_NEAR = 0.0F;
    private static final float NIGHT_FOG_FAR = 16.0F;
    private static final float DAY_FOG_NEAR = 6.0F;
    private static final float DAY_FOG_FAR = 55.0F;

    private static final float NIGHT_MOON_VISIBILITY = 0.1F;
    private static final float DAY_SUN_VISIBILITY = 0.6F;

    private static final float NIGHT_ASH_ATTEMPTS = 9.0F;
    private static final float DAY_ASH_ATTEMPTS = 1.0F;
    private static final int ASH_RADIUS_XZ = 16;
    private static final int ASH_HEIGHT_ABOVE = 8;
    private static final int ASH_HEIGHT_BELOW = 4;
    private static final int ASH_COLOR = 0xFFBFBFBF;

    // How far out from the biome edge the effects fade in/out, and how coarsely we sample that area
    private static final int BLEND_RADIUS = 16;
    private static final int BLEND_STEP = 8;

    private static BlockPos blendCachePos;
    private static float blendCacheFactor;

    // RenderFog is cancellable; returning boolean from a @SubscribeEvent method
    // registers it as a "maybe cancelling" listener - true only when we've overridden it.
    @SubscribeEvent
    public static boolean onRenderFog(ViewportEvent.RenderFog event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        BlockPos pos = event.getCamera().blockPosition();
        float blend = crimsonBlendFactor(level, pos);
        if (blend <= 0.0F) return false;

        float factor = dayFactor(level.getOverworldClockTime());
        float crimsonNear = Mth.lerp(factor, NIGHT_FOG_NEAR, DAY_FOG_NEAR);
        float crimsonFar = Mth.lerp(factor, NIGHT_FOG_FAR, DAY_FOG_FAR);
        event.setNearPlaneDistance(Mth.lerp(blend, event.getNearPlaneDistance(), crimsonNear));
        event.setFarPlaneDistance(Mth.lerp(blend, event.getFarPlaneDistance(), crimsonFar));
        return true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) return;

        float blend = crimsonBlendFactor(level, player.blockPosition());
        if (blend <= 0.0F) return;

        float factor = dayFactor(level.getOverworldClockTime());
        float attempts = blend * Mth.lerp(factor, NIGHT_ASH_ATTEMPTS, DAY_ASH_ATTEMPTS);

        RandomSource random = level.getRandom();
        int wholeAttempts = (int) attempts;
        if (random.nextFloat() < attempts - wholeAttempts) wholeAttempts++;

        for (int i = 0; i < wholeAttempts; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 2 * ASH_RADIUS_XZ;
            double y = player.getY() - ASH_HEIGHT_BELOW + random.nextDouble() * (ASH_HEIGHT_ABOVE + ASH_HEIGHT_BELOW);
            double z = player.getZ() + (random.nextDouble() - 0.5) * 2 * ASH_RADIUS_XZ;
            level.addParticle(new ColoredAshParticleOptions(ASH_COLOR), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    // Sun/moon brightness multiplier; they are drawn outside the terrain fog pass so the mist doesn't hide them
    public static float celestialVisibility(ClientLevel level, BlockPos pos) {
        float blend = crimsonBlendFactor(level, pos);
        if (blend <= 0.0F) return 1.0F;
        float crimsonVisibility = Mth.lerp(dayFactor(level.getOverworldClockTime()), NIGHT_MOON_VISIBILITY, DAY_SUN_VISIBILITY);
        return Mth.lerp(blend, 1.0F, crimsonVisibility);
    }

    public static float starVisibility(ClientLevel level, BlockPos pos) {
        return Mth.lerp(crimsonBlendFactor(level, pos), 1.0F, 0.0F);
    }

    // Fraction (0..1) of a sampled area around pos that is Crimson Plains, used to fade
    // fog/ash/sky effects in and out smoothly instead of snapping at the biome border.
    private static float crimsonBlendFactor(ClientLevel level, BlockPos pos) {
        if (pos.equals(blendCachePos)) return blendCacheFactor;

        int total = 0;
        int inBiome = 0;
        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
        for (int dx = -BLEND_RADIUS; dx <= BLEND_RADIUS; dx += BLEND_STEP) {
            for (int dz = -BLEND_RADIUS; dz <= BLEND_RADIUS; dz += BLEND_STEP) {
                sample.set(pos.getX() + dx, pos.getY(), pos.getZ() + dz);
                total++;
                if (level.getBiome(sample).is(ModBiomes.CRIMSON_PLAINS)) inBiome++;
            }
        }

        blendCacheFactor = (float) inBiome / total;
        blendCachePos = pos;
        return blendCacheFactor;
    }

    // 1.0 = full day, 0.0 = full night, with a smoothstep ramp concentrated around dawn/dusk
    private static float dayFactor(long dayTime) {
        long t = dayTime % 24000L;
        if (t < 0) t += 24000L;
        long diff = Math.abs(t - 6000L);
        diff = Math.min(diff, 24000L - diff);
        return 1.0F - smoothstep(5000.0F, 8000.0F, diff);
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }
}
