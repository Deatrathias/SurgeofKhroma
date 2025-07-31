package net.deatrathias.khroma.gui;

import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.UIReference;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KhromaApertureMenu extends AbstractContainerMenu {

	private ContainerLevelAccess access;

	private DataSlot limitData;

	public KhromaApertureMenu(int containerId, Inventory playerInv) {
		this(containerId, playerInv, ContainerLevelAccess.NULL, DataSlot.standalone());
	}

	public KhromaApertureMenu(int containerId, Inventory playerInv, ContainerLevelAccess access, DataSlot data) {
		super(UIReference.KHROMA_APERTURE.get(), containerId);
		this.access = access;
		limitData = data;
		addDataSlot(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(access, player, BlockReference.KHROMA_APERTURE.get());
	}

	public float getLimit() {
		return ((float) limitData.get() / Integer.MAX_VALUE);
	}

	public void setLimit(float limit) {
		limitData.set(Mth.floor(limit * Integer.MAX_VALUE));
		access.execute(Level::blockEntityChanged);
		broadcastChanges();
	}
}
