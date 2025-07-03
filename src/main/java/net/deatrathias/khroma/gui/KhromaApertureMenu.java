package net.deatrathias.khroma.gui;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KhromaApertureMenu extends AbstractContainerMenu {

	private ContainerLevelAccess access;

	public KhromaApertureMenu(int containerId, Inventory playerInv) {
		this(containerId, playerInv, ContainerLevelAccess.NULL);
	}

	public KhromaApertureMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
		super(RegistryReference.MENU_KHROMA_APERTURE.get(), containerId);
		this.access = access;

	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(access, player, RegistryReference.BLOCK_KHROMA_APERTURE.get());
	}

	public float getLimit() {
		return access.evaluate((level, blockPos) -> {
			BlockEntity be = level.getBlockEntity(blockPos);
			if (be instanceof KhromaApertureBlockEntity kabe)
				return kabe.getLimit();

			return 0f;
		}).orElse(-10f);
	}
}
