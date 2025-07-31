package net.deatrathias.khroma.compat.jei;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.RecipeReference;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;

public class KhromaImbuementCategory implements IRecipeCategory<RecipeHolder<KhromaImbuementRecipe>> {

	private static final String TITLE = "recipe." + SurgeofKhroma.MODID + ".khroma_imbuement";

	private IJeiHelpers helpers;

	private IRecipeHolderType<KhromaImbuementRecipe> recipeType;

	private IDrawable khromaBackground;

	public KhromaImbuementCategory(IJeiHelpers helpers) {
		this.helpers = helpers;
		recipeType = IRecipeType.create(RecipeReference.KHROMA_IMBUEMENT.get());
		khromaBackground = helpers.getGuiHelper().drawableBuilder(SurgeofKhroma.resource("textures/gui/jei/khroma_bg.png"), 0, 0, 30, 29).setTextureSize(30, 29).build();
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<KhromaImbuementRecipe> recipe, IFocusGroup focuses) {
		builder.addAnimatedRecipeArrow(20).setPosition(37, 22);
		builder.addText(Component.translatable("surgeofkhroma.khroma_cost", Mth.floor(recipe.value().getKhromaCost())), getWidth() - 20, 10)
				.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM).setTextAlignment(HorizontalAlignment.RIGHT).setColor(0xFF808080);
	}

	@Override
	public int getWidth() {
		return 95;
	}

	@Override
	public int getHeight() {
		return 59;
	}

	@Override
	public IRecipeHolderType<KhromaImbuementRecipe> getRecipeType() {
		return recipeType;
	}

	@Override
	public Component getTitle() {
		return Component.translatable(TITLE);
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return helpers.getGuiHelper().createDrawableItemLike(BlockReference.KHROMA_IMBUER);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<KhromaImbuementRecipe> recipe, IFocusGroup focuses) {
		builder.addInputSlot(13, 5).add(recipe.value().getIngredient()).setStandardSlotBackground();
		builder.addOutputSlot(72, 21).add(recipe.value().getResult()).setOutputSlotBackground();
		builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 12, 33).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.value().getKhroma()).setBackground(khromaBackground, -7, -6);
	}
}
