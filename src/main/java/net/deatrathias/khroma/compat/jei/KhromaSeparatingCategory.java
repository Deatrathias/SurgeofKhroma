package net.deatrathias.khroma.compat.jei;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class KhromaSeparatingCategory implements IRecipeCategory<KhromaSeparatingRecipe> {
	private static final String TITLE = "recipe." + SurgeofKhroma.MODID + ".khroma_separating";

	private IRecipeType<KhromaSeparatingRecipe> recipeType;

	private IJeiHelpers helpers;

	private IDrawable arrow;

	public KhromaSeparatingCategory(IJeiHelpers helpers) {
		this.helpers = helpers;
		recipeType = IRecipeType.create(SurgeofKhroma.MODID, "khroma_separating", KhromaSeparatingRecipe.class);
		arrow = helpers.getGuiHelper().createDrawable(SurgeofKhroma.resource("textures/gui/jei/khroma_arrows.png"), 36, 0, 36, 35);
	}

	@Override
	public IRecipeType<KhromaSeparatingRecipe> getRecipeType() {
		return recipeType;
	}

	@Override
	public Component getTitle() {
		return Component.translatable(TITLE);
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return helpers.getGuiHelper().createDrawableItemLike(RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, KhromaSeparatingRecipe recipe, IFocusGroup focuses) {
		builder.addInputSlot(37, 5).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.input()).setStandardSlotBackground();
		builder.addOutputSlot(6, 50).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.output1()).setStandardSlotBackground();
		builder.addOutputSlot(68, 50).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.output2()).setStandardSlotBackground();
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, KhromaSeparatingRecipe recipe, IFocusGroup focuses) {
		builder.addDrawable(arrow).setPosition(27, 26);
	}

	@Override
	public @Nullable ResourceLocation getRegistryName(KhromaSeparatingRecipe recipe) {
		return SurgeofKhroma.resource("khroma_separating." + recipe.input().getName());
	}

	@Override
	public int getWidth() {
		return 90;
	}

	@Override
	public int getHeight() {
		return 79;
	}

	public static List<KhromaSeparatingRecipe> generateAllRecipes() {
		List<Khroma> allKhroma = Khroma.allKhroma();
		List<KhromaSeparatingRecipe> result = new ArrayList<KhromaSeparatingRecipe>();
		for (Khroma khroma : allKhroma) {
			if (khroma.countColors() <= 1)
				continue;

			Khroma[] outputs = khroma.separate();
			result.add(new KhromaSeparatingRecipe(khroma, outputs[0], outputs[1]));
		}

		return result;
	}
}
