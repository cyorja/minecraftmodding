package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.Arrays;

/**
 * Brine model: a head with six tentacles hanging beneath it. Source is blockbench/brine.bbmodel.
 * <p>
 * The tentacles sit inside two wrapper parts that aren't in Blockbench: {@code tentacles} tilts the
 * whole cluster backward while the brine moves, and {@code tentacle_ring} inside it slowly spins.
 * To update from a new Blockbench export, paste the head and part0..part5 lines into
 * {@link #createBodyLayer()}, add the tentacles to {@code ring} instead of the root, and subtract
 * {@link #TENTACLE_PIVOT_Y} from each tentacle's Y offset (the second PartPose number).
 */
public class BrineModel extends EntityModel<BrineRenderState> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "brine"), "main");

    // Tentacles hang from the underside of the head, which is where they tilt and spin around
    private static final float TENTACLE_PIVOT_Y = 4.0F;
    private static final int TENTACLE_COUNT = 6;

    private static final float SPIN_SPEED = 0.05F;     // radians per tick, ~6 seconds per turn
    private static final float PULSE_SPEED = 0.15F;    // radians per tick, one open-and-close every ~2 seconds
    private static final float PULSE_AMOUNT = 0.6F;    // tentacles open to 160% and close to 40% of their Blockbench splay
    private static final float TRAIL_STRENGTH = 6.0F;  // radians of tilt per block/tick of speed
    private static final float MAX_TRAIL = 1.0F;       // radians, ~57 degrees

    private final ModelPart head;
    private final ModelPart tentacles;
    private final ModelPart tentacleRing;
    private final ModelPart[] tentacleParts;

    public BrineModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.tentacles = root.getChild("tentacles");
        this.tentacleRing = this.tentacles.getChild("tentacle_ring");
        this.tentacleParts = new ModelPart[TENTACLE_COUNT];
        Arrays.setAll(this.tentacleParts, i -> this.tentacleRing.getChild("part" + i));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tentacles = partdefinition.addOrReplaceChild("tentacles", CubeListBuilder.create(), PartPose.offset(0.0F, TENTACLE_PIVOT_Y, 0.0F));
        PartDefinition ring = tentacles.addOrReplaceChild("tentacle_ring", CubeListBuilder.create(), PartPose.ZERO);

        ring.addOrReplaceChild("part0", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3214F, 8.883F - TENTACLE_PIVOT_Y, 1.342F, 0.3491F, 0.0F, -0.3491F));
        ring.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 8.0F - TENTACLE_PIVOT_Y, 1.0F, 0.3491F, 0.0F, 0.3491F));
        ring.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(0, 16).addBox(0.3214F, -0.883F, 0.342F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 8.0F - TENTACLE_PIVOT_Y, -3.0F, -0.3491F, 0.0F, 0.3491F));
        ring.addOrReplaceChild("part3", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.883F, 7.6786F - TENTACLE_PIVOT_Y, -3.9397F, -0.3491F, 0.0F, -0.3491F));
        ring.addOrReplaceChild("part4", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 7.0603F - TENTACLE_PIVOT_Y, -3.658F, -0.3491F, 0.0F, 0.0F));
        ring.addOrReplaceChild("part5", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 8.0F - TENTACLE_PIVOT_Y, 2.0F, 0.3491F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(BrineRenderState state) {
        // Resets every part to its createBodyLayer() pose, so the motion below adds on top of the Blockbench pose
        super.setupAnim(state);
        float t = state.ageInTicks;

        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);

        // Slow spin of the whole ring under the head
        this.tentacleRing.yRot = t * SPIN_SPEED;

        // Squid-style pulse: every tentacle opens and closes together by scaling its outward splay
        float splay = 1.0F + Mth.sin(t * PULSE_SPEED) * PULSE_AMOUNT;
        for (ModelPart tentacle : this.tentacleParts) {
            tentacle.xRot *= splay;
            tentacle.zRot *= splay;
        }

        // Trail behind while moving: the model faces -Z and +X is its left, so tilting the
        // cluster's bottom toward +Z / +X pushes it behind / to the left of the body
        this.tentacles.xRot = Mth.clamp(state.forwardSpeed * TRAIL_STRENGTH, -MAX_TRAIL, MAX_TRAIL);
        this.tentacles.zRot = Mth.clamp(-state.rightSpeed * TRAIL_STRENGTH, -MAX_TRAIL, MAX_TRAIL);
    }
}
