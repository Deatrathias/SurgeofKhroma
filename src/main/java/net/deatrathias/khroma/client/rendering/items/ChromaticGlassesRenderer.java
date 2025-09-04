package net.deatrathias.khroma.client.rendering.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ChromaticGlassesRenderer implements ICurioRenderer {

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(SurgeofKhroma.resource("chromatic_glasses"), "chromatic_glasses");

	@Override
	public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, MultiBufferSource renderTypeBuffer,
			int packedLight, S renderState, RenderLayerParent<S, M> renderLayerParent, Context context, float yRotation, float xRotation) {

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
		poseStack.mulPose(Axis.XP.rotationDegrees(xRotation));
		poseStack.scale(0.5f, 0.5f, 0.5f);
		VertexConsumer consumer = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(SurgeofKhroma.resource("textures/item/chromatic_glasses.png")));
		Pose pose = poseStack.last();
		consumer.addVertex(pose, -0.5f, -0.6875f, -0.6f).setUv(0, 0.3125f).setColor(0xFFFFFFFF).setLight(packedLight).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, -0.5f, 0f, -0.6f).setUv(0, 1).setColor(0xFFFFFFFF).setLight(packedLight).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, 0f, -0.6f).setUv(1, 1).setColor(0xFFFFFFFF).setLight(packedLight).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, -0.6875f, -0.6f).setUv(1, 0.3125f).setColor(0xFFFFFFFF).setLight(packedLight).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);

		poseStack.popPose();
	}
}
