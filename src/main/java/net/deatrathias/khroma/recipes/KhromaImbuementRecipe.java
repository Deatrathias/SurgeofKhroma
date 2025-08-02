package net.deatrathias.khroma.recipes;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.RecipeReference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class KhromaImbuementRecipe implements Recipe<ItemKhromaRecipeInput> {

	private final Ingredient ingredient;
	private final Khroma khroma;
	private final ItemStack result;
	private final float khromaCost;
	@Nullable
	private PlacementInfo placementInfo;

	public KhromaImbuementRecipe(Ingredient ingredient, Khroma khroma, ItemStack result, float khromaCost) {
		this.ingredient = ingredient;
		this.khroma = khroma;
		this.result = result;
		this.khromaCost = khromaCost;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	public ItemStack getResult() {
		return result;
	}

	public float getKhromaCost() {
		return khromaCost;
	}

	@Override
	public boolean matches(ItemKhromaRecipeInput input, Level level) {
		return ingredient.test(input.item()) && !result.is(input.item().getItem()) && input.khroma().contains(khroma);
	}

	@Override
	public ItemStack assemble(ItemKhromaRecipeInput input, Provider registries) {
		return result.copy();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	protected ItemStack result() {
		return result;
	}

	public static record ImbuementRecipeDisplay(SlotDisplay ingredient, Khroma khroma, float khromaCost, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
		public static final MapCodec<ImbuementRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						SlotDisplay.CODEC.fieldOf("ingredient").forGetter(ImbuementRecipeDisplay::ingredient),
						Khroma.CODEC.fieldOf("khroma").forGetter(ImbuementRecipeDisplay::khroma),
						Codec.FLOAT.fieldOf("khromaCost").forGetter(ImbuementRecipeDisplay::khromaCost),
						SlotDisplay.CODEC.fieldOf("result").forGetter(ImbuementRecipeDisplay::result),
						SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ImbuementRecipeDisplay::craftingStation))
						.apply(instance, ImbuementRecipeDisplay::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ImbuementRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
				SlotDisplay.STREAM_CODEC,
				ImbuementRecipeDisplay::ingredient,
				Khroma.STREAM_CODEC,
				ImbuementRecipeDisplay::khroma,
				ByteBufCodecs.FLOAT,
				ImbuementRecipeDisplay::khromaCost,
				SlotDisplay.STREAM_CODEC,
				ImbuementRecipeDisplay::result,
				SlotDisplay.STREAM_CODEC,
				ImbuementRecipeDisplay::craftingStation,
				ImbuementRecipeDisplay::new);

		public static final Type<ImbuementRecipeDisplay> TYPE = new Type<KhromaImbuementRecipe.ImbuementRecipeDisplay>(MAP_CODEC, STREAM_CODEC);

		@Override
		public Type<? extends RecipeDisplay> type() {
			return TYPE;
		}

		@Override
		public boolean isEnabled(FeatureFlagSet flags) {
			return this.ingredient.isEnabled(flags) && RecipeDisplay.super.isEnabled(flags);
		}
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new ImbuementRecipeDisplay(ingredient.display(), khroma, khromaCost, new SlotDisplay.ItemStackSlotDisplay(result),
				new SlotDisplay.ItemSlotDisplay(BlockReference.KHROMA_IMBUER.asItem())));
	}

	public static class KhromaImbuementSerializer implements RecipeSerializer<KhromaImbuementRecipe> {
		public static final MapCodec<KhromaImbuementRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
				.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(KhromaImbuementRecipe::getIngredient), Khroma.CODEC.fieldOf("khroma").forGetter(KhromaImbuementRecipe::getKhroma),
						ItemStack.CODEC.fieldOf("result").forGetter(KhromaImbuementRecipe::getResult), Codec.FLOAT.fieldOf("khromacost").forGetter(KhromaImbuementRecipe::getKhromaCost))
				.apply(instance, KhromaImbuementRecipe::new));

		private final StreamCodec<RegistryFriendlyByteBuf, KhromaImbuementRecipe> STREAM_CODEC = StreamCodec.of(this::toNetwork, this::fromNetwork);

		@Override
		public MapCodec<KhromaImbuementRecipe> codec() {
			return CODEC;
		}

		@SuppressWarnings("deprecation")
		@Override
		public StreamCodec<RegistryFriendlyByteBuf, KhromaImbuementRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private KhromaImbuementRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			Khroma khroma = Khroma.fromInt(buffer.readInt());
			ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
			float khromaCost = buffer.readFloat();
			return new KhromaImbuementRecipe(ingredient, khroma, result, khromaCost);
		}

		private void toNetwork(RegistryFriendlyByteBuf buffer, KhromaImbuementRecipe recipe) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
			buffer.writeInt(recipe.khroma.asInt());
			ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
			buffer.writeFloat(recipe.khromaCost);
		}
	}

	@Override
	public RecipeSerializer<KhromaImbuementRecipe> getSerializer() {
		return RecipeReference.SERIALIZER_KHROMA_IMBUEMENT.get();
	}

	@Override
	public RecipeType<KhromaImbuementRecipe> getType() {
		return RecipeReference.KHROMA_IMBUEMENT.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		if (placementInfo == null)
			placementInfo = PlacementInfo.create(ingredient);
		return placementInfo;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.FURNACE_MISC;
	}

}
