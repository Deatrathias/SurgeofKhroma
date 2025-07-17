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

public class KhromaCombiningCategory implements IRecipeCategory<KhromaCombiningRecipe> {

	private static final String TITLE = "recipe." + SurgeofKhroma.MODID + ".khroma_combining";

	private IRecipeType<KhromaCombiningRecipe> recipeType;

	private IJeiHelpers helpers;

	private IDrawable arrow;

	public KhromaCombiningCategory(IJeiHelpers helpers) {
		this.helpers = helpers;
		recipeType = IRecipeType.create(SurgeofKhroma.MODID, "khroma_combining", KhromaCombiningRecipe.class);
		arrow = helpers.getGuiHelper().createDrawable(SurgeofKhroma.resource("textures/gui/jei/khroma_arrows.png"), 0, 0, 36, 33);
	}

	@Override
	public IRecipeType<KhromaCombiningRecipe> getRecipeType() {
		return recipeType;
	}

	@Override
	public Component getTitle() {
		return Component.translatable(TITLE);
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return helpers.getGuiHelper().createDrawableItemLike(RegistryReference.ITEM_BLOCK_KHROMA_COMBINER);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, KhromaCombiningRecipe recipe, IFocusGroup focuses) {
		builder.addInputSlot(6, 5).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.input1()).setStandardSlotBackground();
		builder.addInputSlot(68, 5).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.input2()).setStandardSlotBackground();
		builder.addOutputSlot(37, 50).add(JeiKhromaPlugin.INGREDIENT_KHROMA, recipe.output()).setStandardSlotBackground();
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, KhromaCombiningRecipe recipe, IFocusGroup focuses) {
		builder.addDrawable(arrow).setPosition(27, 12);
	}

	@Override
	public @Nullable ResourceLocation getRegistryName(KhromaCombiningRecipe recipe) {
		return SurgeofKhroma.resource("khroma_combining." + recipe.input1().getName() + "_" + recipe.input2().getName());
	}

	@Override
	public int getWidth() {
		return 90;
	}

	@Override
	public int getHeight() {
		return 79;
	}

	public static List<KhromaCombiningRecipe> generateAllRecipes() {
		List<Khroma> allKhroma = Khroma.allKhroma();
		List<KhromaCombiningRecipe> result = new ArrayList<KhromaCombiningRecipe>();
		for (int i = 1; i < allKhroma.size(); i++) {
			for (int j = i + 1; j < allKhroma.size(); j++) {
				Khroma input1 = allKhroma.get(i);
				Khroma input2 = allKhroma.get(j);
				if (input1.contains(input2) || input2.contains(input1))
					continue;

				Khroma output = Khroma.combine(input1, input2);
				result.add(new KhromaCombiningRecipe(input1, input2, output));
			}
		}

		return result;
	}
}
