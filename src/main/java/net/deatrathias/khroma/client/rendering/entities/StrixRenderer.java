package net.deatrathias.khroma.client.rendering.entities;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.client.models.StrixModel;
import net.deatrathias.khroma.client.rendering.entities.states.StrixRenderState;
import net.deatrathias.khroma.entities.Strix;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StrixRenderer extends MobRenderer<Strix, StrixRenderState, StrixModel> {
	private static final ResourceLocation TEXTURE = SurgeofKhroma.resource("textures/entity/strix/strix.png");

	public StrixRenderer(Context context) {
		super(context, new StrixModel(context.bakeLayer(ClientOnlyReference.STRIX)), 0.8f);
	}

	@Override
	public ResourceLocation getTextureLocation(StrixRenderState renderState) {
		return TEXTURE;
	}

	@Override
	public StrixRenderState createRenderState() {
		return new StrixRenderState();
	}

	@Override
	public void extractRenderState(Strix entity, StrixRenderState reusedState, float partialTick) {
		super.extractRenderState(entity, reusedState, partialTick);
		reusedState.standAnimationState.copyFrom(entity.standAnimationState);
		reusedState.flyAnimationState.copyFrom(entity.flyAnimationState);
		reusedState.hoverAnimationState.copyFrom(entity.hoverAnimationState);
	}
}
