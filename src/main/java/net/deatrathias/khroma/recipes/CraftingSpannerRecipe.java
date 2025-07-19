package net.deatrathias.khroma.recipes;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.items.SpannerItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class CraftingSpannerRecipe extends ShapedRecipe {

	private static final Map<ResourceLocation, Integer> INGOT_TO_COLOR = ImmutableMap.of(
			RegistryReference.ITEM_KHROMETAL_INGOT_RED.getId(), 0xFFFF0000,
			RegistryReference.ITEM_KHROMETAL_INGOT_GREEN.getId(), 0xFF00FF00,
			RegistryReference.ITEM_KHROMETAL_INGOT_BLUE.getId(), 0xFF0000FF,
			RegistryReference.ITEM_KHROMETAL_INGOT_WHITE.getId(), 0xFFFFFFFF,
			RegistryReference.ITEM_KHROMETAL_INGOT_BLACK.getId(), 0xFF202020);

	private static final int MISSING_COLOR = 0xFFFF00FF;

	public CraftingSpannerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
		super(group, category, pattern, result);
	}

	public CraftingSpannerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
		super(group, category, pattern, result, showNotification);
	}

	public CraftingSpannerRecipe(ShapedRecipe recipe) {
		this(recipe.group(), recipe.category(), recipe.pattern, recipe.result, recipe.showNotification());
	}

	@Override
	public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
		return RegistryReference.RECIPE_SERIALIZER_CRAFTING_SPANNER.get();
	}

	@Override
	public ItemStack assemble(CraftingInput input, Provider provider) {
		ItemStack assembled = super.assemble(input, provider);
		var ingredient = input.getItem(1, 2);
		int baseColor = INGOT_TO_COLOR.getOrDefault(ingredient.getItemHolder().getKey().location(), MISSING_COLOR);
		ingredient = input.getItem(1, 1);
		int middleColor = INGOT_TO_COLOR.getOrDefault(ingredient.getItemHolder().getKey().location(), MISSING_COLOR);
		ingredient = input.getItem(2, 0);
		int topColor = INGOT_TO_COLOR.getOrDefault(ingredient.getItemHolder().getKey().location(), MISSING_COLOR);
		ingredient = input.getItem(0, 0);
		int bottomColor = INGOT_TO_COLOR.getOrDefault(ingredient.getItemHolder().getKey().location(), MISSING_COLOR);
		assembled.set(RegistryReference.DATA_COMPONENT_SPANNER_COLORS, new SpannerItem.SpannerColors(baseColor, middleColor, topColor, bottomColor));

		return assembled;
	}

	public static class Serializer implements RecipeSerializer<CraftingSpannerRecipe> {
		private static final MapCodec<CraftingSpannerRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(CraftingSpannerRecipe::new, Function.identity());
		private static final StreamCodec<RegistryFriendlyByteBuf, CraftingSpannerRecipe> STREAM_CODEC = ShapedRecipe.Serializer.STREAM_CODEC.map(CraftingSpannerRecipe::new, Function.identity());

		@Override
		public MapCodec<CraftingSpannerRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CraftingSpannerRecipe> streamCodec() {
			return STREAM_CODEC;
		}

	}
}
