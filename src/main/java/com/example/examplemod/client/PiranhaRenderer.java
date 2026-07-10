package com.example.examplemod.client;

import com.example.examplemod.entity.PiranhaEntity;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.Identifier;

public class PiranhaRenderer extends MobRenderer<PiranhaEntity, TropicalFishRenderState, TropicalFishLargeModel> {

    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/entity/fish/tropical_b.png");

    public PiranhaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TropicalFishLargeModel(ctx.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE)), 0.4f);
    }

    @Override
    public TropicalFishRenderState createRenderState() {
        return new TropicalFishRenderState();
    }

    @Override
    public Identifier getTextureLocation(TropicalFishRenderState state) {
        return TEXTURE;
    }
}
