package net.deatrathias.khroma.gui;

import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.registries.UIReference;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KhromaImbuerMenu extends AbstractContainerMenu {
	public static final int INGREDIENT_SLOT = 0;
	public static final int RESULT_SLOT = 1;
	public static final int SLOT_COUNT = 2;
	public static final int DATA_COUNT = 5;
	public static final int INV_SLOT_START = 2;
	public static final int INV_SLOT_END = 29;
	public static final int USE_ROW_SLOT_START = 29;
	public static final int USE_ROW_SLOT_END = 38;
	private final Container container;
	private final ContainerData data;
	protected final Level level;

	public KhromaImbuerMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public KhromaImbuerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
		super(UIReference.KHROMA_IMBUER.get(), containerId);
		checkContainerSize(container, SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;
		this.level = playerInventory.player.level();
		addSlot(new Slot(container, INGREDIENT_SLOT, 49, 35));
		addSlot(new ImbuerResultSlot(playerInventory.player, container, RESULT_SLOT, 104, 35));

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}

		for (int row = 0; row < 9; row++) {
			this.addSlot(new Slot(playerInventory, row, 8 + row * 18, 142));
		}

		this.addDataSlots(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			result = stack.copy();
			if (index == RESULT_SLOT) {
				if (!moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(stack, result);
			} else if (index != INGREDIENT_SLOT && index != RESULT_SLOT) {
				if (!moveItemStackTo(stack, INGREDIENT_SLOT, INGREDIENT_SLOT + 1, false)) {
					if (index >= INV_SLOT_START && index < INV_SLOT_END) {
						if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false))
							return ItemStack.EMPTY;
					} else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END && !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (stack.getCount() == result.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, stack);
		}

		return result;
	}

	public float getProgress() {
		return (float) data.get(0) / Integer.MAX_VALUE;
	}

	public KhromaThroughput getKhromaThroughput() {
		Khroma khroma = Khroma.fromInt(data.get(1));
		return new KhromaThroughput(khroma, data.get(2) / 100f);
	}

	public float getSoftLimit() {
		return data.get(3) / 100f;
	}

	public float getEffectiveRate() {
		return data.get(4) / 100f;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

}
