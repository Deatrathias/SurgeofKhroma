package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemOutputModuleBlockEntity extends BlockEntity implements IItemHandler {

	private ItemStack item = ItemStack.EMPTY;

	public ItemOutputModuleBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_ITEM_OUTPUT_MODULE.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!item.isEmpty())
			output.store("Item", ItemStack.CODEC, item);
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? item : ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	public ItemStack modularInsertItem(ItemStack stack, boolean simulate) {
		if (item.isEmpty()) {
			if (!simulate) {
				item = stack.copy();
				contentUpdated();
			}
			return ItemStack.EMPTY;
		} else if (!ItemStack.isSameItemSameComponents(stack, item) || item.getCount() >= item.getMaxStackSize()) {
			return stack;
		} else {
			int totalCount = Math.min(stack.getCount() + item.getCount(), item.getMaxStackSize());
			int remaining = Math.max(0, stack.getCount() + item.getCount() - totalCount);
			if (!simulate) {
				item.setCount(totalCount);
				contentUpdated();
			}
			return stack.copyWithCount(remaining);
		}

	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack result;
		if (amount >= item.getCount()) {
			result = item.copy();
			if (!simulate && !item.isEmpty()) {
				item = ItemStack.EMPTY;
				contentUpdated();
			}
		} else {
			result = item.copyWithCount(amount);
			if (!simulate) {
				item.shrink(amount);
				contentUpdated();
			}
		}

		return result;
	}

	private void contentUpdated() {
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		if (item != null && !item.isEmpty()) {
			Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), item);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 99;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slot == 0;
	}
}
