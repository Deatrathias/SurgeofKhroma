package net.deatrathias.khroma.registries;

import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.recipes.CraftingSpannerRecipe;
import net.deatrathias.khroma.recipes.KhromaFabricationRecipe;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeReference {

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SurgeofKhroma.MODID);
	public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, SurgeofKhroma.MODID);

	public static final Supplier<RecipeType<KhromaImbuementRecipe>> KHROMA_IMBUEMENT = RECIPE_TYPES.register("khroma_imbuement", registryName -> RecipeType.simple(registryName));
	public static final Supplier<RecipeSerializer<KhromaImbuementRecipe>> SERIALIZER_KHROMA_IMBUEMENT = RECIPE_SERIALIZERS.register("khroma_imbuement", KhromaImbuementRecipe.Serializer::new);

	public static final Supplier<RecipeType<CraftingSpannerRecipe>> CRAFTING_SPANNER = RECIPE_TYPES.register("crafting_spanner", registryName -> RecipeType.simple(registryName));
	public static final Supplier<RecipeSerializer<CraftingSpannerRecipe>> SERIALIZER_CRAFTING_SPANNER = RECIPE_SERIALIZERS.register("crafting_spanner", CraftingSpannerRecipe.Serializer::new);

	public static final Supplier<RecipeType<KhromaFabricationRecipe>> KHROMA_FABRICATION = RECIPE_TYPES.register("khroma_fabrication", registryName -> RecipeType.simple(registryName));
	public static final Supplier<RecipeSerializer<KhromaFabricationRecipe>> SERIALIZER_KHROMA_FABRICATION = RECIPE_SERIALIZERS.register("khroma_fabrication", KhromaFabricationRecipe.Serializer::new);
	public static final Supplier<RecipeBookCategory> CATEGORY_KHROMA_FABRICATION = RECIPE_BOOK_CATEGORIES.register("khroma_fabrication", RecipeBookCategory::new);
}
