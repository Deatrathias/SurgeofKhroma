package net.deatrathias.khroma.client.rendering.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;

import net.deatrathias.khroma.blockentities.ItemOutputModuleBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemOutputModuleBlockEntityRenderer implements BlockEntityRenderer<ItemOutputModuleBlockEntity> {
	private ItemRenderer itemRenderer;
	private EntityRenderDispatcher entityRenderDispatcher;
	private RandomSource random = RandomSource.create();

	public ItemOutputModuleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		itemRenderer = context.getItemRenderer();
		entityRenderDispatcher = context.getEntityRenderer();
	}

	@Override
	public void render(ItemOutputModuleBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
		ItemStack item = blockEntity.getStackInSlot(0);
		if (item != null && !item.isEmpty()) {

			int seed = ItemClusterRenderState.getSeedForItemStack(item);
			int count = ItemClusterRenderState.getRenderedAmount(item.getCount());
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.scale(0.5f, 0.5f, 0.5f);

			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
			itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), seed);

			random.setSeed(seed);
			for (int i = 1; i < count; i++) {
				poseStack.pushPose();
				float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
				float y = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
				float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
				poseStack.translate(x, y, z);
				itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}

			poseStack.popPose();
		}
	}

}
