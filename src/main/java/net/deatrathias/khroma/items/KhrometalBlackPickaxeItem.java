package net.deatrathias.khroma.items;

import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class KhrometalBlackPickaxeItem extends PickaxeItem {

	public KhrometalBlackPickaxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		int result = super.getEnchantmentLevel(stack, enchantment);

		if (enchantment.is(Enchantments.FORTUNE))
			return Math.min(Math.max(enchantment.value().getMaxLevel(), result), result + 2);

		return result;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(EnchantmentTags.MINING_EXCLUSIVE) && !enchantment.is(Enchantments.FORTUNE))
			return false;
		return super.supportsEnchantment(stack, enchantment);
	}
}
