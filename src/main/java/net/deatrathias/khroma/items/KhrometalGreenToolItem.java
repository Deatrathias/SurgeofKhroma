package net.deatrathias.khroma.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class KhrometalGreenToolItem extends Item {

	public KhrometalGreenToolItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (entity.tickCount % 300 == 0)
			stack.setDamageValue(stack.getDamageValue() - 1);
	}
}
