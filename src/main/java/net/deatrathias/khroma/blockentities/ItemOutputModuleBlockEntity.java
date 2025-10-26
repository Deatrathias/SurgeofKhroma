package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ItemOutputModuleBlockEntity extends BlockEntity implements ItemOwner {

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
	
	public static class ItemHandler extends ItemStackResourceHandler {
		public final ItemOutputModuleBlockEntity blockEntity;
		
		public ItemHandler(ItemOutputModuleBlockEntity blockEntity, Direction direction) {
			this.blockEntity = blockEntity;
		}

		@Override
		protected ItemStack getStack() {
			return blockEntity.item;
		}

		@Override
		protected void setStack(ItemStack stack) {
			blockEntity.item = stack;
			blockEntity.contentUpdated();
		}
		
		@Override
		public boolean isValid(int index, ItemResource resource) {
			return false;
		}
		
		 @Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}
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
	
	public ItemStack getItem() {
		return item;
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
	public Level level() {
		return level;
	}

	@Override
	public Vec3 position() {
		return worldPosition.getCenter();
	}

	@Override
	public float getVisualRotationYInDegrees() {
		return 0;
	}
}
