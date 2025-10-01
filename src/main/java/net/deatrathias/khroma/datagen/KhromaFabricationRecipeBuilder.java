package net.deatrathias.khroma.datagen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.recipes.KhromaFabricationRecipe;
import net.deatrathias.khroma.recipes.KhromaFabricationRecipePattern;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class KhromaFabricationRecipeBuilder implements RecipeBuilder {
	private final HolderGetter<Item> items;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	private final Item result;
	private final int count;
	private final Khroma khroma;
	private final float khromaCost;

	public KhromaFabricationRecipeBuilder(HolderGetter<Item> items, Khroma khroma, float khromaCost, Item result, int count) {
		this.items = items;
		this.result = result;
		this.count = count;
		this.khroma = khroma;
		this.khromaCost = khromaCost;
	}

	public KhromaFabricationRecipeBuilder(HolderGetter<Item> items, Khroma khroma, float khromaCost, Item result) {
		this(items, khroma, khromaCost, result, 1);
	}

	public KhromaFabricationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	@Override
	public KhromaFabricationRecipeBuilder group(String groupName) {
		return this;
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public KhromaFabricationRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return this.define(symbol, Ingredient.of(this.items.getOrThrow(tag)));
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public KhromaFabricationRecipeBuilder define(Character symbol, ItemLike item) {
		return this.define(symbol, Ingredient.of(item));
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public KhromaFabricationRecipeBuilder define(Character symbol, Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else if (symbol == 'X') {
			throw new IllegalArgumentException("Symbol 'X' is reserved and cannot be defined");
		} else {
			this.key.put(symbol, ingredient);
			return this;
		}
	}

	/**
	 * Adds a new entry to the patterns for this recipe.
	 */
	public KhromaFabricationRecipeBuilder pattern(String pattern) {
		if (pattern.length() != 3)
			throw new IllegalArgumentException("Line must be 3 characters long!");
		if (rows.size() == 1 && pattern.charAt(1) != 'X')
			throw new IllegalArgumentException("Center of the pattern must be X!");
		this.rows.add(pattern);
		return this;
	}

	@Override
	public Item getResult() {
		return result;
	}

	@Override
	public void save(RecipeOutput output, ResourceKey<Recipe<?>> resourceKey) {
		KhromaFabricationRecipePattern pattern = this.ensureValid(resourceKey);
		Advancement.Builder advancement = output.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
				.rewards(AdvancementRewards.Builder.recipe(resourceKey))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement::addCriterion);
		KhromaFabricationRecipe recipe = new KhromaFabricationRecipe(
				pattern,
				khroma,
				khromaCost,
				new ItemStack(result, count));
		output.accept(resourceKey, recipe, advancement.build(resourceKey.location().withPrefix("recipes/khroma_fabrication/")));
	}

	private KhromaFabricationRecipePattern ensureValid(ResourceKey<Recipe<?>> recipe) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipe.location());
		} else {
			return KhromaFabricationRecipePattern.of(this.key, this.rows);
		}
	}
}
