package net.deatrathias.khroma.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.gui.KhromaImbuerScreen;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeMap;

@JeiPlugin
public class JeiKhromaPlugin implements IModPlugin {

	private KhromaImbuementCategory khromaImbuementCategory;

	private KhromaCombiningCategory khromaCombiningCategory;

	private KhromaSeparatingCategory khromaSeparatingCategory;

	private static RecipeMap recipes = RecipeMap.EMPTY;

	public static void setRecipes(RecipeMap recipes) {
		JeiKhromaPlugin.recipes = recipes;
	}

	public static final IIngredientType<Khroma> INGREDIENT_KHROMA = new IIngredientType<Khroma>() {
		@Override
		public Class<? extends Khroma> getIngredientClass() {
			return Khroma.class;
		}

		@Override
		public String getUid() {
			return SurgeofKhroma.MODID + ".khroma";
		}
	};

	@Override
	public ResourceLocation getPluginUid() {
		return SurgeofKhroma.resource("jei");
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		registration.register(INGREDIENT_KHROMA, Khroma.allKhroma().subList(1, 32), new KhromaIngredientHelper(), new KhromaIngredientRenderer(), Khroma.CODEC);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		khromaImbuementCategory = new KhromaImbuementCategory(registration.getJeiHelpers());
		registration.addRecipeCategories(khromaImbuementCategory);
		khromaCombiningCategory = new KhromaCombiningCategory(registration.getJeiHelpers());
		registration.addRecipeCategories(khromaCombiningCategory);
		khromaSeparatingCategory = new KhromaSeparatingCategory(registration.getJeiHelpers());
		registration.addRecipeCategories(khromaSeparatingCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		var imbuementRecipes = recipes.byType(RegistryReference.RECIPE_KHROMA_IMBUEMENT.get()).stream().filter(holder -> !holder.value().getIngredient().isEmpty()).toList();
		registration.addRecipes(khromaImbuementCategory.getRecipeType(), imbuementRecipes);

		registration.addRecipes(khromaCombiningCategory.getRecipeType(), KhromaCombiningCategory.generateAllRecipes());
		registration.addRecipes(khromaSeparatingCategory.getRecipeType(), KhromaSeparatingCategory.generateAllRecipes());
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

		registration.addRecipeTransferHandler(new KhromaImbuementRecipeTransfer(khromaImbuementCategory.getRecipeType(), registration.getTransferHelper()), khromaImbuementCategory.getRecipeType());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(khromaImbuementCategory.getRecipeType(), RegistryReference.ITEM_BLOCK_KHROMA_IMBUER);
		registration.addCraftingStation(khromaCombiningCategory.getRecipeType(), RegistryReference.ITEM_BLOCK_KHROMA_COMBINER);
		registration.addCraftingStation(khromaSeparatingCategory.getRecipeType(), RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(KhromaImbuerScreen.class, 72, 35, 22, 14, khromaImbuementCategory.getRecipeType());
	}
}
