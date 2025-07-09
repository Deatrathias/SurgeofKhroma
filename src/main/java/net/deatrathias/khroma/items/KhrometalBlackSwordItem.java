package net.deatrathias.khroma.items;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class KhrometalBlackSwordItem extends SwordItem {

	public KhrometalBlackSwordItem(Tier p_tier, Properties p_properties) {
		super(p_tier, p_properties);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		int result = super.getEnchantmentLevel(stack, enchantment);

		if (enchantment.is(Enchantments.LOOTING))
			return Math.min(Math.max(enchantment.value().getMaxLevel(), result), result + 2);

		return result;
	}

	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, RegistryLookup<Enchantment> lookup) {
		ItemEnchantments result = super.getAllEnchantments(stack, lookup);

		var looting = lookup.getOrThrow(Enchantments.LOOTING);
		if (result.getLevel(looting) < looting.value().getMaxLevel()) {
			ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(result);
			int level = result.getLevel(looting);
			mutable.set(looting, Math.min(Math.max(looting.value().getMaxLevel(), level), level + 2));
			return mutable.toImmutable();
		}
		return result;
	}
}
