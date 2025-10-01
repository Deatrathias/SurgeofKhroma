package net.deatrathias.khroma.recipes;

import java.util.List;

import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class KhromaFabricationInput implements RecipeInput {

	private final List<ItemStack> items;

	private final List<Khroma> khromas;

	public KhromaFabricationInput(List<ItemStack> items, List<Khroma> khromas) {
		this.items = items;
		this.khromas = khromas;
	}

	@Override
	public ItemStack getItem(int index) {
		return items.get(index);
	}

	@Override
	public int size() {
		return items.size();
	}

	public Khroma getKhroma(Khroma required) {
		Khroma result = Khroma.EMPTY;
		for (var khroma : khromas) {
			if (khroma.contains(required) && (result == Khroma.EMPTY || result.countColors() > khroma.countColors()))
				result = khroma;
		}

		return result;
	}
}
