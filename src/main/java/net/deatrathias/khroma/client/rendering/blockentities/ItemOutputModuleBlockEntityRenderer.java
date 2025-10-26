package net.deatrathias.khroma.client.rendering.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.HashCommon;
import net.deatrathias.khroma.blockentities.ItemOutputModuleBlockEntity;
import net.deatrathias.khroma.client.rendering.blockentities.states.ItemOutputModuleBlockEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemOutputModuleBlockEntityRenderer implements BlockEntityRenderer<ItemOutputModuleBlockEntity, ItemOutputModuleBlockEntityRenderState> {
	private final ItemModelResolver itemModelResolver;
	private RandomSource random = RandomSource.create();

	public ItemOutputModuleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}
	
	@Override
	public ItemOutputModuleBlockEntityRenderState createRenderState() {
		return new ItemOutputModuleBlockEntityRenderState();
	}
	
	@Override
	public void extractRenderState(ItemOutputModuleBlockEntity blockEntity,
			ItemOutputModuleBlockEntityRenderState renderState, float partialTick, Vec3 cameraPosition,
			CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
		renderState.content = new ItemClusterRenderState();
		ItemStack stack = blockEntity.getItem();
		itemModelResolver.updateForTopItem(renderState.content.item, stack, ItemDisplayContext.GROUND, blockEntity.getLevel(), blockEntity, HashCommon.long2int(blockEntity.getBlockPos().asLong()));
		renderState.content.count = ItemClusterRenderState.getRenderedAmount(stack.getCount());
		renderState.content.seed = ItemClusterRenderState.getSeedForItemStack(stack);
	}

	@Override
	public void submit(ItemOutputModuleBlockEntityRenderState renderState, PoseStack poseStack,
			SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
		ItemClusterRenderState item = renderState.content;
		if (item != null && !item.item.isEmpty()) {

			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.scale(0.5f, 0.5f, 0.5f);

			poseStack.mulPose(cameraRenderState.orientation);
			ItemEntityRenderer.submitMultipleFromCount(poseStack, nodeCollector, renderState.lightCoords, item, random);

			poseStack.popPose();
		}
	}

}
