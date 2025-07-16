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
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class JeiKhromaPlugin implements IModPlugin {

	private KhromaImbuementCategory khromaImbuementCategory;

	private KhromaCombiningCategory khromaCombiningCategory;

	private KhromaSeparatingCategory khromaSeparatingCategory;

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
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		var imbuementRecipes = recipeManager.getAllRecipesFor(RegistryReference.RECIPE_KHROMA_IMBUEMENT.get()).stream().filter(recipe -> !recipe.value().getIngredient().hasNoItems()).toList();
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
		registration.addRecipeCatalyst(RegistryReference.ITEM_BLOCK_KHROMA_IMBUER, khromaImbuementCategory.getRecipeType());
		registration.addRecipeCatalyst(RegistryReference.ITEM_BLOCK_KHROMA_COMBINER, khromaCombiningCategory.getRecipeType());
		registration.addRecipeCatalyst(RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR, khromaSeparatingCategory.getRecipeType());
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(KhromaImbuerScreen.class, 72, 35, 22, 14, khromaImbuementCategory.getRecipeType());
	}
}
