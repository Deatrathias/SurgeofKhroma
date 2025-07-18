package net.deatrathias.khroma.items;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class KhrometalWhitePickaxeItem extends Item {

	public KhrometalWhitePickaxeItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		int result = super.getEnchantmentLevel(stack, enchantment);

		if (enchantment.is(Enchantments.SILK_TOUCH))
			return 1;

		return result;
	}

	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, RegistryLookup<Enchantment> lookup) {
		ItemEnchantments result = super.getAllEnchantments(stack, lookup);

		var silkTouch = lookup.getOrThrow(Enchantments.SILK_TOUCH);
		if (result.getLevel(silkTouch) == 0) {
			ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(result);
			mutable.set(silkTouch, 1);
			return mutable.toImmutable();
		}
		return result;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(EnchantmentTags.MINING_EXCLUSIVE))
			return false;
		return super.supportsEnchantment(stack, enchantment);
	}
}
