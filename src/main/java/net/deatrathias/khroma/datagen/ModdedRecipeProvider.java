package net.deatrathias.khroma.datagen;

import java.util.List;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public abstract class ModdedRecipeProvider extends RecipeProvider {

	protected ModdedRecipeProvider(Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	protected abstract String getModId();

	protected String getModdedRecipeName(ItemLike itemLike) {
		return getModId() + ":" + getSimpleRecipeName(itemLike);
	}

	@Override
	protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
		this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, getModdedRecipeName(packed), null, getModdedRecipeName(unpacked), null);
	}

	@Override
	protected void nineBlockStorageRecipesWithCustomPacking(
			RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String packedGroup) {
		this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, packedName, packedGroup, getModdedRecipeName(unpacked), null);
	}

	@Override
	protected void nineBlockStorageRecipesRecipesWithCustomUnpacking(
			RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String unpackedName, String unpackedGroup) {
		this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, getModdedRecipeName(packed), null, unpackedName, unpackedGroup);
	}

	@Override
	protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String packedGroup,
			String unpackedName, String unpackedGroup) {
		super.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, packedName, packedGroup, unpackedName, unpackedGroup);
	}

	protected <T extends AbstractCookingRecipe> void oreCooking(
			RecipeSerializer<T> serializer,
			AbstractCookingRecipe.Factory<T> recipeFactory,
			List<ItemLike> ingredients,
			RecipeCategory category,
			ItemLike result,
			float experience,
			int cookingTime,
			String group,
			String suffix) {
		for (ItemLike itemlike : ingredients) {
			SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, serializer, recipeFactory)
					.group(group)
					.unlockedBy(getHasName(itemlike), this.has(itemlike))
					.save(this.output, getModdedRecipeName(result) + suffix + "_" + getItemName(itemlike));
		}
	}

	protected String getModdedConversionRecipeName(ItemLike result, ItemLike ingredient) {
		return getModId() + ":" + getItemName(result) + "_from_" + getItemName(ingredient);
	}
}
