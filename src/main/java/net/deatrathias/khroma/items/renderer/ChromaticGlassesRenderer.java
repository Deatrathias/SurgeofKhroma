package net.deatrathias.khroma.items.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class ChromaticGlassesRenderer implements ICurioRenderer {

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "chromatic_glasses"), "chromatic_glasses");

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent,
			MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		matrixStack.pushPose();
		matrixStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
		matrixStack.mulPose(Axis.XP.rotationDegrees(headPitch));
		matrixStack.scale(0.5f, 0.5f, 0.5f);
		VertexConsumer consumer = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "textures/item/chromatic_glasses.png")));
		Pose pose = matrixStack.last();
		consumer.addVertex(pose, -0.5f, -0.6875f, -0.6f).setUv(0, 0.3125f).setColor(0xFFFFFFFF).setLight(light).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, -0.5f, 0f, -0.6f).setUv(0, 1).setColor(0xFFFFFFFF).setLight(light).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, 0f, -0.6f).setUv(1, 1).setColor(0xFFFFFFFF).setLight(light).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);
		consumer.addVertex(pose, 0.5f, -0.6875f, -0.6f).setUv(1, 0.3125f).setColor(0xFFFFFFFF).setLight(light).setNormal(pose, 0, 0, 1).setOverlay(OverlayTexture.NO_OVERLAY);

		matrixStack.popPose();
	}

}
