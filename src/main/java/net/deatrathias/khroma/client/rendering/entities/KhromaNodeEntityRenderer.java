package net.deatrathias.khroma.client.rendering.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.client.rendering.entities.states.KhromaNodeEntityRenderState;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class KhromaNodeEntityRenderer extends EntityRenderer<KhromaNodeEntity, KhromaNodeEntityRenderState> {
	
	private static final ResourceLocation TEXTURE = SurgeofKhroma.resource("textures/entity/node.png");

	public KhromaNodeEntityRenderer(Context context) {
		super(context);
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
	public void submit(KhromaNodeEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
		poseStack.pushPose();
		poseStack.translate(0, 0.5f, 0);
		poseStack.mulPose(cameraRenderState.orientation);
		var renderer = new Renderer(renderState.khroma.getTint());
		nodeCollector.submitCustomGeometry(poseStack, ClientOnlyReference.RENDER_KHROMA_NODE.apply(TEXTURE), renderer);
		poseStack.popPose();
	}

	private class Renderer implements SubmitNodeCollector.CustomGeometryRenderer {
		private int color;
		
		public Renderer(int color) {
			this.color = color;
		}

		@Override
		public void render(Pose pose, VertexConsumer consumer) {
			int light = 15728880;
			
			consumer.addVertex(pose, -0.5f, -0.5f, 0).setUv(0, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
			consumer.addVertex(pose, 0.5f, -0.5f, 0).setUv(1, 0).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
			consumer.addVertex(pose, 0.5f, 0.5f, 0).setUv(1, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
			consumer.addVertex(pose, -0.5f, 0.5f, 0).setUv(0, 1).setLight(light).setColor(color).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		}
	}


	public static boolean canSeeNodes() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return false;
		return player.getAttribute(RegistryReference.ATTRIBUTE_CAN_SEE_NODES).getValue() > 0;
	}
}
