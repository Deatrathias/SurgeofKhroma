package net.deatrathias.khroma.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;

import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ChromaticGlassesRenderer implements AccessoryRenderer {

	private ModelPart baseModel;

	private static final ResourceLocation TEXTURE = SurgeofKhroma.resource("textures/entity/equipment/humanoid/chromatic_glasses.png");

	public ChromaticGlassesRenderer() {
		baseModel = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_ARMOR.head());
	}

	@Override
	public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
			EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
		collector.submitModelPart(baseModel, matrices, model.renderType(TEXTURE), entityState.lightCoords, OverlayTexture.NO_OVERLAY, null);
	}

}
