// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class brine<T extends BrineEntity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "brine"), "main");
	private final ModelPart head;
	private final ModelPart part0;
	private final ModelPart part1;
	private final ModelPart part2;
	private final ModelPart part3;
	private final ModelPart part4;
	private final ModelPart part5;

	public brine(ModelPart root) {
		this.head = root.getChild("head");
		this.part0 = root.getChild("part0");
		this.part1 = root.getChild("part1");
		this.part2 = root.getChild("part2");
		this.part3 = root.getChild("part3");
		this.part4 = root.getChild("part4");
		this.part5 = root.getChild("part5");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition part0 = partdefinition.addOrReplaceChild("part0", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3214F, 8.883F, 1.342F, 0.3491F, 0.0F, -0.3491F));

		PartDefinition part1 = partdefinition.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 8.0F, 1.0F, 0.3491F, 0.0F, 0.3491F));

		PartDefinition part2 = partdefinition.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(0, 16).addBox(0.3214F, -0.883F, 0.342F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 8.0F, -3.0F, -0.3491F, 0.0F, 0.3491F));

		PartDefinition part3 = partdefinition.addOrReplaceChild("part3", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.883F, 7.6786F, -3.9397F, -0.3491F, 0.0F, -0.3491F));

		PartDefinition part4 = partdefinition.addOrReplaceChild("part4", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 7.0603F, -3.658F, -0.3491F, 0.0F, 0.0F));

		PartDefinition part5 = partdefinition.addOrReplaceChild("part5", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 8.0F, 2.0F, 0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(BrineEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		part5.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}