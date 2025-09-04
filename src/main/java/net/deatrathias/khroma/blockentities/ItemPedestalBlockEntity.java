package net.deatrathias.khroma.blockentities;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import com.mojang.math.Axis;

import net.deatrathias.khroma.blocks.machine.ItemPedestalBlock;
import net.deatrathias.khroma.entities.PlacedItemEntity;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.util.EightDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemPedestalBlockEntity extends BlockEntity {

	private EntityReference<PlacedItemEntity> placedItemEntity;

	public ItemPedestalBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_ITEM_PEDESTAL.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		placedItemEntity = EntityReference.read(input, "PlacedItem");
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		EntityReference.store(placedItemEntity, output, "PlacedItem");
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}

	public ItemStack placeItem(ItemStack stack, boolean simulate) {
		if (stack == null || stack.isEmpty())
			return stack;
		ItemEntity itemEntity = getItemEntity();
		if (itemEntity == null) {
			if (!simulate)
				createItemEntity(stack);
			return ItemStack.EMPTY;
		}

		ItemStack placed = itemEntity.getItem();
		if (ItemStack.isSameItemSameComponents(stack, placed)) {
			int maxStack = placed.getMaxStackSize();
			int addedCount = stack.getCount() + placed.getCount();
			if (!simulate) {
				itemEntity.setItem(placed.copyWithCount(Math.min(addedCount, maxStack)));
			}
			return stack.copyWithCount(Math.max(0, addedCount - maxStack));
		} else
			return stack;
	}

	public ItemStack takeItem(int count, boolean simulate) {
		ItemEntity itemEntity = getItemEntity();
		if (itemEntity == null)
			return ItemStack.EMPTY;

		ItemStack stack = itemEntity.getItem();
		if (stack.getCount() <= count) {
			if (!simulate && !level.isClientSide) {
				itemEntity.discard();
				placedItemEntity = null;
				setChanged();
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
			}
			return stack;
		} else {
			if (!simulate) {
				itemEntity.setItem(stack.copyWithCount(stack.getCount() - count));
			}
			return stack.copyWithCount(count);
		}
	}

	private Vec3 getItemPosition() {
		EightDirection direction = getBlockState().getValue(ItemPedestalBlock.FACING);
		var adjusted = Axis.YP.rotationDegrees(-direction.convertToDegrees()).transform(new Vector3d(0, 0.1875, -0.1875));
		return worldPosition.getCenter().add(adjusted.x, adjusted.y, adjusted.z);
	}

	private void createItemEntity(ItemStack stack) {
		if (level.isClientSide)
			return;

		if (stack == null || stack.isEmpty())
			return;

		Vec3 position = getItemPosition();
		PlacedItemEntity itemEntity = new PlacedItemEntity(level, position.x, position.y, position.z, stack);
		itemEntity.setNeverPickUp();
		itemEntity.setInvulnerable(true);
		itemEntity.setUnlimitedLifetime();
		level.addFreshEntity(itemEntity);

		PlacedItemEntity existing = getItemEntity();
		if (existing != null)
			existing.discard();
		placedItemEntity = new EntityReference<>(itemEntity);
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	public void createItemEntityForPlayerPickup(ItemStack stack, Player player) {
		Vec3 position = getItemPosition();
		ItemEntity itemEntity = new ItemEntity(level, position.x, position.y, position.z, stack);
		itemEntity.setNoPickUpDelay();
		level.addFreshEntity(itemEntity);
		itemEntity.playerTouch(player);
	}

	private PlacedItemEntity getItemEntity() {
		if (placedItemEntity == null)
			return null;
		return placedItemEntity.getEntity(level, PlacedItemEntity.class);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		ItemEntity itemEntity = getItemEntity();
		if (itemEntity != null) {
			ItemEntity drop = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem());
			level.addFreshEntity(drop);
			itemEntity.discard();
		}
	}

	private ItemStack getPlacedItemStack() {
		if (placedItemEntity == null)
			return ItemStack.EMPTY;

		ItemEntity itemEntity = placedItemEntity.getEntity(level, PlacedItemEntity.class);
		if (itemEntity == null)
			return ItemStack.EMPTY;

		return itemEntity.getItem();
	}

	public void onStateChanged() {
		ItemEntity itemEntity = getItemEntity();
		if (itemEntity != null) {
			itemEntity.snapTo(getItemPosition());
			itemEntity.hasImpulse = true;
		}
	}

	public static class ItemHandler implements IItemHandler {

		private final ItemPedestalBlockEntity blockEntity;

		public ItemHandler(final ItemPedestalBlockEntity blockEntity, @Nullable Direction side) {
			this.blockEntity = blockEntity;
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return blockEntity.getPlacedItemStack();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot != 0)
				return stack;
			return blockEntity.placeItem(stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot != 0)
				return ItemStack.EMPTY;
			return blockEntity.takeItem(amount, simulate);
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

}
