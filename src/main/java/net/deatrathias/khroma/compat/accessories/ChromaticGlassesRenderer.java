package net.deatrathias.khroma.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChromaticGlassesRenderer implements AccessoryRenderer {

	private HumanoidArmorModel<HumanoidRenderState> baseModel;

	private static final ResourceLocation TEXTURE = SurgeofKhroma.resource("textures/entity/equipment/humanoid/chromatic_glasses.png");

	public ChromaticGlassesRenderer() {
		baseModel = new HumanoidArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
	}

	@Override
	public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices, EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light,
			float partialTicks) {
		if (!(renderState instanceof HumanoidRenderState))
			return;
		VertexConsumer buffer = multiBufferSource.getBuffer(model.renderType(TEXTURE));

		baseModel.setupAnim((HumanoidRenderState) renderState);
		baseModel.renderToBuffer(matrices, buffer, light, OverlayTexture.NO_OVERLAY);
	}

}
