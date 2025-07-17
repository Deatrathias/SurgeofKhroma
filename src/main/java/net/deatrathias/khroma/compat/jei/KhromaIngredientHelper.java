package net.deatrathias.khroma.compat.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class KhromaIngredientHelper implements IIngredientHelper<Khroma> {

	@Override
	public IIngredientType<Khroma> getIngredientType() {
		return JeiKhromaPlugin.INGREDIENT_KHROMA;
	}

	@Override
	public String getDisplayName(Khroma ingredient) {
		return Component.translatable(ingredient.getLocalizedName()).getString();
	}

	@Override
	public Object getUid(Khroma ingredient, UidContext context) {
		return ingredient.asInt();
	}

	@Override
	public ResourceLocation getResourceLocation(Khroma ingredient) {
		return SurgeofKhroma.resource("khroma." + ingredient.getName());
	}

	@Override
	public Khroma copyIngredient(Khroma ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(Khroma ingredient) {
		return ingredient.getName();
	}

	@Override
	public String getDisplayModId(Khroma ingredient) {
		return SurgeofKhroma.MODID;
	}
}
