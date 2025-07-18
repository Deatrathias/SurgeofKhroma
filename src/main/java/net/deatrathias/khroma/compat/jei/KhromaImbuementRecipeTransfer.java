package net.deatrathias.khroma.compat.jei;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.gui.KhromaImbuerMenu;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;

public class KhromaImbuementRecipeTransfer implements IRecipeTransferHandler<KhromaImbuerMenu, RecipeHolder<KhromaImbuementRecipe>> {

	private IRecipeHolderType<KhromaImbuementRecipe> recipeType;

	private IRecipeTransferHandlerHelper helper;

	private IRecipeTransferHandler<KhromaImbuerMenu, RecipeHolder<KhromaImbuementRecipe>> defaultHander;

	public KhromaImbuementRecipeTransfer(IRecipeHolderType<KhromaImbuementRecipe> recipeType, IRecipeTransferHandlerHelper helper) {
		this.recipeType = recipeType;
		this.helper = helper;
		IRecipeTransferInfo<KhromaImbuerMenu, RecipeHolder<KhromaImbuementRecipe>> info = new IRecipeTransferInfo<KhromaImbuerMenu, RecipeHolder<KhromaImbuementRecipe>>() {

			@Override
			public IRecipeHolderType<KhromaImbuementRecipe> getRecipeType() {
				return recipeType;
			}

			@Override
			public List<Slot> getRecipeSlots(KhromaImbuerMenu container, RecipeHolder<KhromaImbuementRecipe> recipe) {
				return List.of(container.getSlot(KhromaImbuerMenu.INGREDIENT_SLOT));
			}

			@Override
			public Optional<MenuType<KhromaImbuerMenu>> getMenuType() {
				return Optional.of(RegistryReference.MENU_KHROMA_IMBUER.get());
			}

			@Override
			public List<Slot> getInventorySlots(KhromaImbuerMenu container, RecipeHolder<KhromaImbuementRecipe> recipe) {
				return container.slots.subList(KhromaImbuerMenu.INV_SLOT_START, KhromaImbuerMenu.USE_ROW_SLOT_END);
			}

			@Override
			public Class<? extends KhromaImbuerMenu> getContainerClass() {
				return KhromaImbuerMenu.class;
			}

			@Override
			public boolean canHandle(KhromaImbuerMenu container, RecipeHolder<KhromaImbuementRecipe> recipe) {
				return true;
			}
		};
		defaultHander = helper.createUnregisteredRecipeTransferHandler(info);
	}

	@Override
	public Class<? extends KhromaImbuerMenu> getContainerClass() {
		return KhromaImbuerMenu.class;
	}

	@Override
	public Optional<MenuType<KhromaImbuerMenu>> getMenuType() {
		return Optional.of(RegistryReference.MENU_KHROMA_IMBUER.get());
	}

	@Override
	public IRecipeHolderType<KhromaImbuementRecipe> getRecipeType() {
		return recipeType;
	}

	@Override
	public @Nullable IRecipeTransferError transferRecipe(KhromaImbuerMenu container, RecipeHolder<KhromaImbuementRecipe> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer,
			boolean doTransfer) {
		Khroma khroma = container.getKhromaThroughput().getKhroma();
		if (!khroma.contains(recipe.value().getKhroma()))
			return helper.createUserErrorForMissingSlots(Component.translatable("jei.surgeofkhroma.error.incompatible_khroma"), List.of(recipeSlots.getSlotViews().get(2)));

		return defaultHander.transferRecipe(container, recipe, recipeSlots, player, maxTransfer, doTransfer);
	}

}
