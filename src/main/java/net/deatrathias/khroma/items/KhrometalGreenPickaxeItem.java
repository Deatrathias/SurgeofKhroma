package net.deatrathias.khroma.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public class KhrometalGreenPickaxeItem extends PickaxeItem implements KhrometalGreenToolItem {

	public KhrometalGreenPickaxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		repairTick(stack, level, entity, slotId, isSelected);
	}
}
