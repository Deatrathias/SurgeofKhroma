package net.deatrathias.khroma.client.rendering.items;

import org.jetbrains.annotations.Nullable;

import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.items.GuideItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class KhromancerArchiveRenderer extends GeoItemRenderer<GuideItem> {

	public KhromancerArchiveRenderer() {
		super(new DefaultedItemGeoModel<GuideItem>(SurgeofKhroma.resource("khromancer_archive")).withAltTexture(SurgeofKhroma.resource("geckolib/khromancer_archive")));
	}
	
	@Override
	public void addRenderData(GuideItem animatable, RenderData relatedObject, GeoRenderState renderState) {
		renderState.addGeckolibData(animatable.openTicket, relatedObject.itemStack().get(DataComponentRegistry.BOOK_OPEN));
	}
	
	@Override
	public void actuallyRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model,
			@Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
			boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		
		poseStack.pushPose();
		var perspective = renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
		if (perspective != null && perspective.leftHand())
			poseStack.scale(-1, 1, 1);
		super.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight,
				packedOverlay, renderColor);
		poseStack.popPose();
	}
}
