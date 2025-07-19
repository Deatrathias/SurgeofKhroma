package net.deatrathias.khroma.datagen;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

public class RecipeOutputWrapper<B extends Recipe<?>, T extends Recipe<?>> implements RecipeOutput {

	private RecipeOutput output;

	private Function<B, T> conversionFunction;

	public RecipeOutputWrapper(RecipeOutput output, Function<B, T> conversionFunction) {
		this.output = output;
		this.conversionFunction = conversionFunction;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		output.accept(key, conversionFunction.apply((B) recipe), advancement, conditions);
	}

	@Override
	public Builder advancement() {
		return output.advancement();
	}

	@Override
	public void includeRootAdvancement() {
		output.includeRootAdvancement();
	}

}
