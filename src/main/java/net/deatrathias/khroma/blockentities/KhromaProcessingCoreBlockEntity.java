package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class KhromaProcessingCoreBlockEntity extends BlockEntity {

	private ItemStack itemContent = ItemStack.EMPTY;

	public KhromaProcessingCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_KHROMA_PROCESSING_CORE.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		itemContent = input.read("itemContent", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("itemContent", ItemStack.CODEC, itemContent);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemContent);
	}
}
