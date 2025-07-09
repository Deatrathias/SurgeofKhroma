package net.deatrathias.khroma.entities.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KhromaNodeEntityRenderer extends EntityRenderer<KhromaNodeEntity> {

	public KhromaNodeEntityRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(KhromaNodeEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "textures/entity/node.png");
	}

	@Override
	public void render(KhromaNodeEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(p_entity)));
		poseStack.translate(0, 0.5f, 0);
		poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		PoseStack.Pose pose = poseStack.last();
		int color = p_entity.getKhroma().getTint();
		int light = 15728880;
		consumer.addVertex(pose, -0.5f, -0.5f, 0).setUv(0, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, -0.5f, 0).setUv(1, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, 0.5f, 0).setUv(1, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, -0.5f, 0.5f, 0).setUv(0, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
