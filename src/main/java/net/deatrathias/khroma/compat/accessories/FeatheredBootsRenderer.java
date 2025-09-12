package net.deatrathias.khroma.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;

import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.items.FeatheredBootsItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FeatheredBootsRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<FeatheredBootsItem, R> implements AccessoryRenderer {

	public FeatheredBootsRenderer() {
		super(new DefaultedItemGeoModel<FeatheredBootsItem>(SurgeofKhroma.resource("feathered_boots")).withAltTexture(SurgeofKhroma.resource("geckolib/accessories/feathered_boots")));
	}

	@Override
	public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices, EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light,
			float partialTicks) {

	}

	@Override
	public void addRenderData(FeatheredBootsItem animatable, RenderData relatedObject, R renderState) {
		renderState.addGeckolibData(ClientOnlyReference.IS_ON_GROUND, relatedObject.entity().onGround());
	}
}
