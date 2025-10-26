package net.deatrathias.khroma.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;

import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.items.FeatheredBootsItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FeatheredBootsRenderer<R extends AvatarRenderState & GeoRenderState> extends GeoArmorRenderer<FeatheredBootsItem, R> implements AccessoryRenderer {

	public FeatheredBootsRenderer() {
		super(new DefaultedItemGeoModel<FeatheredBootsItem>(SurgeofKhroma.resource("feathered_boots")).withAltTexture(SurgeofKhroma.resource("geckolib/accessories/feathered_boots")));
	}
	
	@Override
	public void addRenderData(FeatheredBootsItem animatable, RenderData relatedObject, R renderState,
			float partialTick) {
		renderState.addGeckolibData(ClientOnlyReference.IS_ON_GROUND, relatedObject.entity().onGround());
	}

	@Override
	public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
			EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
		// TODO Auto-generated method stub
		
	}
}
