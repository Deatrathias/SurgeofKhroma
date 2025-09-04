package net.deatrathias.khroma.client.models;

import net.deatrathias.khroma.client.animations.StrixAnimation;
import net.deatrathias.khroma.client.rendering.entities.states.StrixRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class StrixModel extends EntityModel<StrixRenderState> {
	private final ModelPart head;
	private final KeyframeAnimation standAnimation;
	private final KeyframeAnimation flyAnimation;
	private final KeyframeAnimation hoverAnimation;

	public StrixModel(ModelPart root) {
		super(root);
		this.head = root.getChild("base").getChild("head");
		this.standAnimation = StrixAnimation.STAND.bake(root);
		this.flyAnimation = StrixAnimation.FLY.bake(root);
		this.hoverAnimation = StrixAnimation.HOVER.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(42, 18).addBox(-4.0F, 1.8F, -3.5F, 8.0F, 10.0F, 0.0F), PartPose.offset(0.0F, -2.8F, -5.0F));

		base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, -1.0F, 12.0F, 23.0F, 9.0F), PartPose.offsetAndRotation(0.0F, 10.8F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(42, 0).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 8.0F), PartPose.offset(0.0F, -1.8F, 0.5F));

		head.addOrReplaceChild("right_plumicorn", CubeListBuilder.create().texOffs(42, 28).mirror().addBox(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 0.0F),
				PartPose.offsetAndRotation(-3.0F, -7.0F, -3.1F, 0.0F, 0.0F, 0.5236F));

		head.addOrReplaceChild("left_plumicorn", CubeListBuilder.create().texOffs(42, 28).addBox(-1.0F, -3.0F, -1.0F, 8.0F, 3.0F, 0.0F),
				PartPose.offsetAndRotation(3.0F, -7.0F, -3.1F, 0.0F, 0.0F, -0.5236F));

		head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(58, 18).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -6.0F, -4.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition left_leg = base.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(33, 2).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 1.0F)
				.texOffs(37, 2).addBox(0.0F, 6.0F, -3.0F, 1.0F, 1.0F, 5.0F), PartPose.offset(4.0F, 19.8F, 7.0F));

		left_leg.addOrReplaceChild("left_talon_left", CubeListBuilder.create().texOffs(40, 5).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 2.0F),
				PartPose.offsetAndRotation(0.5F, 7.0F, -0.5F, 0.0F, -0.7854F, 0.0F));

		left_leg.addOrReplaceChild("left_talon_right", CubeListBuilder.create().texOffs(40, 5).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 2.0F),
				PartPose.offsetAndRotation(0.5F, 7.0F, -0.5F, 0.0F, 0.7854F, 0.0F));

		PartDefinition right_leg = base.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(33, 2).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 1.0F)
				.texOffs(37, 2).addBox(-1.0F, 6.0F, -3.0F, 1.0F, 1.0F, 5.0F), PartPose.offset(-4.0F, 19.8F, 7.0F));

		right_leg.addOrReplaceChild("right_talon_right", CubeListBuilder.create().texOffs(40, 5).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 2.0F),
				PartPose.offsetAndRotation(-0.5F, 7.0F, -0.5F, 0.0F, 0.7854F, 0.0F));

		right_leg.addOrReplaceChild("right_talon_left", CubeListBuilder.create().texOffs(40, 5).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 1.0F, 2.0F),
				PartPose.offsetAndRotation(-0.5F, 7.0F, -0.5F, 0.0F, -0.7854F, 0.0F));

		PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, -0.0025F, -4.5F, 2.0F, 11.0F, 7.0F)
				.texOffs(18, 34).addBox(2.0F, -0.0025F, 2.5F, 0.0F, 11.0F, 5.0F), PartPose.offset(6.0F, -2.8F, -5.0F));

		left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(28, 32).addBox(-2.0F, 0.0F, -5.0F, 2.0F, 8.0F, 7.0F)
				.texOffs(46, 34).addBox(0.0F, 0.0F, 2.0F, 0.0F, 8.0F, 5.0F), PartPose.offset(2.0F, 10.9975F, 0.5F));

		PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-2.0F, -0.0025F, -4.5F, 2.0F, 11.0F, 7.0F)
				.texOffs(18, 34).addBox(-2.0F, -0.0025F, 2.5F, 0.0F, 11.0F, 5.0F), PartPose.offset(-6.0F, -2.8F, -5.0F));

		right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(28, 32).mirror().addBox(0.0F, 0.0F, -5.0F, 2.0F, 8.0F, 7.0F)
				.texOffs(46, 34).addBox(0.0F, 0.0F, 2.0F, 0.0F, 8.0F, 5.0F), PartPose.offset(-2.0F, 10.9975F, 0.5F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(StrixRenderState renderState) {
		super.setupAnim(renderState);
		head.xRot = (float) Math.toRadians(renderState.xRot);
		head.yRot = (float) Math.toRadians(renderState.yRot);
		standAnimation.apply(renderState.standAnimationState, renderState.ageInTicks);
		flyAnimation.apply(renderState.flyAnimationState, renderState.ageInTicks);
		hoverAnimation.apply(renderState.hoverAnimationState, renderState.ageInTicks);
	}
}
