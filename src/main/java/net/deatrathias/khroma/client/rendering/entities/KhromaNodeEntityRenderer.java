package net.deatrathias.khroma.client.rendering.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.client.rendering.entities.states.KhromaNodeEntityRenderState;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class KhromaNodeEntityRenderer extends EntityRenderer<KhromaNodeEntity, KhromaNodeEntityRenderState> {

	public KhromaNodeEntityRenderer(Context context) {
		super(context);
	}

	public ResourceLocation getTextureLocation(KhromaNodeEntity entity) {
		return SurgeofKhroma.resource("textures/entity/node.png");
	}

	@Override
	public KhromaNodeEntityRenderState createRenderState() {
		return new KhromaNodeEntityRenderState();
	}

	@Override
	public void extractRenderState(KhromaNodeEntity p_entity, KhromaNodeEntityRenderState reusedState, float partialTick) {
		super.extractRenderState(p_entity, reusedState, partialTick);
		reusedState.khroma = p_entity.getKhroma();
		reusedState.level = p_entity.getNodeLevel();
	}

	@Override
	public void render(KhromaNodeEntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		VertexConsumer consumer = bufferSource.getBuffer(ClientOnlyReference.RENDER_KHROMA_NODE.apply(SurgeofKhroma.resource("textures/entity/node.png")));
		poseStack.translate(0, 0.5f, 0);
		poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		PoseStack.Pose pose = poseStack.last();
		int color = renderState.khroma.getTint();
		int light = 15728880;
		consumer.addVertex(pose, -0.5f, -0.5f, 0).setUv(0, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, -0.5f, 0).setUv(1, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, 0.5f, 0).setUv(1, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, -0.5f, 0.5f, 0).setUv(0, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		poseStack.popPose();

		super.render(renderState, poseStack, bufferSource, packedLight);
	}

	public static boolean canSeeNodes() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return false;
		return player.getAttribute(RegistryReference.ATTRIBUTE_CAN_SEE_NODES).getValue() > 0;
	}
}
