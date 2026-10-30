package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.BrineEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.blaze.BlazeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

// Reuses the blaze body (head + orbiting rods) with an ocean-blue skin
public class BrineRenderer extends MobRenderer<BrineEntity, LivingEntityRenderState, BlazeModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/brine.png");

    public BrineRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlazeModel(ctx.bakeLayer(ModelLayers.BLAZE)), 0.5f);
    }

    // Glows faintly in dark monuments, like a blaze does in the Nether
    @Override
    protected int getBlockLightLevel(BrineEntity entity, BlockPos pos) {
        return 12;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
