package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class KhromaApertureBlockEntity extends BlockEntity implements MenuProvider {

	private static final Component CONTAINER_TITLE = Component.translatable("container." + SurgeofKhroma.MODID + ".khroma_aperture");

	private float limit;

	protected final DataSlot limitData = new DataSlot() {

		@Override
		public void set(int value) {
			KhromaApertureBlockEntity.this.limit = (float) value / Integer.MAX_VALUE;
		}

		@Override
		public int get() {
			return Mth.floor(KhromaApertureBlockEntity.this.limit * Integer.MAX_VALUE);
		}
	};

	public KhromaApertureBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_KHROMA_APERTURE.get(), pos, blockState);
		limit = 1;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		limit = input.getFloatOr("limit", 1f);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putFloat("limit", limit);
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new KhromaApertureMenu(containerId, playerInventory, ContainerLevelAccess.create(level, worldPosition), limitData);
	}

	@Override
	public Component getDisplayName() {
		return CONTAINER_TITLE;
	}

	public float getLimit() {
		return limit;
	}

}
