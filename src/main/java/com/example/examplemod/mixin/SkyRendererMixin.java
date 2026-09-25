package com.example.examplemod.mixin;

import com.example.examplemod.client.CrimsonAtmosphereHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void dimCelestialsInMist(ClientLevel level, float partialTicks, Camera camera,
                                     SkyRenderState state, CallbackInfo ci) {
        // rainBrightness is the alpha shared by the sun and moon
        state.rainBrightness *= CrimsonAtmosphereHandler.celestialVisibility(level, camera.blockPosition());
        state.starBrightness *= CrimsonAtmosphereHandler.starVisibility(level, camera.blockPosition());
    }
}
