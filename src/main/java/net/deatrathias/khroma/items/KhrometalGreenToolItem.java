package net.deatrathias.khroma.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface KhrometalGreenToolItem {

	default void repairTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (entity.tickCount % 300 == 0)
			stack.setDamageValue(stack.getDamageValue() - 1);
	}
}
