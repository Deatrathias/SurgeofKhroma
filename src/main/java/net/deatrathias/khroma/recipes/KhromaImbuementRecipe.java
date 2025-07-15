package net.deatrathias.khroma.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KhromaImbuementRecipe implements Recipe<ItemKhromaRecipeInput> {

	private final Ingredient ingredient;
	private final Khroma khroma;
	private final ItemStack result;
	private final float khromaCost;

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
		return ingredient.test(input.item()) && input.khroma().contains(khroma);
	}

	@Override
	public ItemStack assemble(ItemKhromaRecipeInput input, Provider registries) {
		return result.copy();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		ingredients.add(this.ingredient);
		return ingredients;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(Provider registries) {
		return result;
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
	public RecipeSerializer<?> getSerializer() {
		return RegistryReference.RECIPE_SERIALIZER_KHROMA_IMBUEMENT.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RegistryReference.RECIPE_KHROMA_IMBUEMENT.get();
	}

}
