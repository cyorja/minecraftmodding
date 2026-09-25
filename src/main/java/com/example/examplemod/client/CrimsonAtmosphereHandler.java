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

    // RenderFog is cancellable; returning boolean from a @SubscribeEvent method
    // registers it as a "maybe cancelling" listener - true only when we've overridden it.
    @SubscribeEvent
    public static boolean onRenderFog(ViewportEvent.RenderFog event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        BlockPos pos = event.getCamera().blockPosition();
        if (!level.getBiome(pos).is(ModBiomes.CRIMSON_PLAINS)) return false;

        float factor = dayFactor(level.getOverworldClockTime());
        event.setNearPlaneDistance(Mth.lerp(factor, NIGHT_FOG_NEAR, DAY_FOG_NEAR));
        event.setFarPlaneDistance(Mth.lerp(factor, NIGHT_FOG_FAR, DAY_FOG_FAR));
        return true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) return;

        if (!level.getBiome(player.blockPosition()).is(ModBiomes.CRIMSON_PLAINS)) return;

        float factor = dayFactor(level.getOverworldClockTime());
        float attempts = Mth.lerp(factor, NIGHT_ASH_ATTEMPTS, DAY_ASH_ATTEMPTS);

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
        if (!level.getBiome(pos).is(ModBiomes.CRIMSON_PLAINS)) return 1.0F;
        return Mth.lerp(dayFactor(level.getOverworldClockTime()), NIGHT_MOON_VISIBILITY, DAY_SUN_VISIBILITY);
    }

    public static float starVisibility(ClientLevel level, BlockPos pos) {
        return level.getBiome(pos).is(ModBiomes.CRIMSON_PLAINS) ? 0.0F : 1.0F;
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
