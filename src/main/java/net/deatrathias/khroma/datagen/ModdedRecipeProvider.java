package net.deatrathias.khroma.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public abstract class ModdedRecipeProvider extends RecipeProvider {

	public ModdedRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries);
	}

	protected abstract String getModId();

	@Override
	protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries) {
		final Set<ResourceLocation> set = Sets.newHashSet();
		final List<CompletableFuture<?>> list = new ArrayList<>();
		this.buildRecipes(new RecipeOutput() {
			@Override
			public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
				resourceLocation = ResourceLocation.fromNamespaceAndPath(getModId(), resourceLocation.getPath());
				if (advancement != null) {

					advancement = rebuildAdvancement(advancement);
				}
				if (!set.add(resourceLocation)) {
					throw new IllegalStateException("Duplicate recipe " + resourceLocation);
				} else {
					list.add(DataProvider.saveStable(output, registries, Recipe.CONDITIONAL_CODEC, Optional.of(new net.neoforged.neoforge.common.conditions.WithConditions<>(recipe, conditions)),
							recipePathProvider.json(resourceLocation)));
					if (advancement != null) {
						list.add(DataProvider.saveStable(output, registries, Advancement.CONDITIONAL_CODEC,
								Optional.of(new net.neoforged.neoforge.common.conditions.WithConditions<>(advancement.value(), conditions)), advancementPathProvider.json(advancement.id())));
					}
				}
			}

			@SuppressWarnings("removal")
			@Override
			public Advancement.Builder advancement() {
				return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
			}

			protected ResourceLocation convert(ResourceLocation rl) {
				return ResourceLocation.fromNamespaceAndPath(getModId(), rl.getPath());
			}

			protected AdvancementHolder rebuildAdvancement(AdvancementHolder advancementHolder) {
				Advancement advancement = advancementHolder.value();
				var builder = advancement();
				for (var entry : advancement.criteria().entrySet())
					builder.addCriterion(entry.getKey(), entry.getValue());
				advancement.display().ifPresent(display -> builder.display(display));
				builder.requirements(advancement.requirements());
				builder.rewards(AdvancementRewards.Builder.recipe(convert(advancement.rewards().recipes().get(0))));

				return builder.build(convert(advancementHolder.id()));
			}
		}, registries);
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

}
