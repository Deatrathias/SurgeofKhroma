package net.deatrathias.khroma.client.rendering.items;

import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.mojang.blaze3d.vertex.PoseStack;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.items.GuideItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class KhromancerArchiveRenderer extends GeoItemRenderer<GuideItem> {

	public KhromancerArchiveRenderer() {
		super(new DefaultedItemGeoModel<GuideItem>(SurgeofKhroma.resource("khromancer_archive")).withAltTexture(SurgeofKhroma.resource("geckolib/khromancer_archive")));
	}
	
	@Override
	public void addRenderData(GuideItem animatable, RenderData relatedObject, GeoRenderState renderState, float partialTicks) {
		renderState.addGeckolibData(animatable.openTicket, relatedObject.itemStack().get(DataComponentRegistry.BOOK_OPEN));
	}
	
	@Override
	public void submit(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, int outlineColor) {
		
		poseStack.pushPose();
		var perspective = renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
		if (perspective != null && perspective.leftHand())
			poseStack.scale(-1, 1, 1);
		super.submit(renderState, poseStack, renderTasks, outlineColor);
		poseStack.popPose();
	}
}
