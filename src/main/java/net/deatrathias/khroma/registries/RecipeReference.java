package net.deatrathias.khroma.registries;

import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.recipes.CraftingSpannerRecipe;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeReference {

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SurgeofKhroma.MODID);

	public static final Supplier<RecipeType<KhromaImbuementRecipe>> KHROMA_IMBUEMENT = RECIPE_TYPES.register("khroma_imbuement",
			() -> RecipeType.simple(SurgeofKhroma.resource("khroma_imbuement")));
	public static final Supplier<RecipeSerializer<KhromaImbuementRecipe>> SERIALIZER_KHROMA_IMBUEMENT = RECIPE_SERIALIZERS.register("khroma_imbuement",
			KhromaImbuementRecipe.KhromaImbuementSerializer::new);

	public static final Supplier<RecipeType<CraftingSpannerRecipe>> CRAFTING_SPANNER = RECIPE_TYPES.register("crafting_spanner",
			() -> RecipeType.simple(SurgeofKhroma.resource("crafting_spanner")));
	public static final Supplier<RecipeSerializer<CraftingSpannerRecipe>> SERIALIZER_CRAFTING_SPANNER = RECIPE_SERIALIZERS.register("crafting_spanner",
			CraftingSpannerRecipe.Serializer::new);

}
