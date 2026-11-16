package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.BrineEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BrineRenderer extends MobRenderer<BrineEntity, BrineRenderState, BrineModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/brine.png");

    public BrineRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BrineModel(ctx.bakeLayer(BrineModel.LAYER)), 0.5f);
    }

    // Glows faintly in dark monuments, like a blaze does in the Nether
    @Override
    protected int getBlockLightLevel(BrineEntity entity, BlockPos pos) {
        return 12;
    }

    @Override
    public BrineRenderState createRenderState() {
        return new BrineRenderState();
    }

    @Override
    public void extractRenderState(BrineEntity entity, BrineRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        // Turn the world-space movement into "forward" and "to the right" relative to the body,
        // using the same facing math as Entity.calculateViewVector (forward = (-sin, 0, cos))
        Vec3 velocity = entity.getTrailVelocity(partialTicks);
        float yaw = state.bodyRot * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw);
        float cos = Mth.cos(yaw);
        state.forwardSpeed = (float) (-velocity.x * sin + velocity.z * cos);
        state.rightSpeed = (float) (-velocity.x * cos - velocity.z * sin);
    }

    @Override
    public Identifier getTextureLocation(BrineRenderState state) {
        return TEXTURE;
    }
}
