package net.deatrathias.khroma.recipes;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.registries.RecipeReference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KhromaFabricationRecipe implements Recipe<KhromaFabricationInput> {

	private final KhromaFabricationRecipePattern pattern;
	private final Khroma khroma;
	private final float khromaCost;
	private final ItemStack result;
	@Nullable
	private PlacementInfo placementInfo;

	public KhromaFabricationRecipe(KhromaFabricationRecipePattern pattern, Khroma khroma, float khromaCost, ItemStack result) {
		this.pattern = pattern;
		this.khroma = khroma;
		this.khromaCost = khromaCost;
		this.result = result;
	}

	public KhromaFabricationRecipePattern getPattern() {
		return pattern;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	public float getKhromaCost() {
		return khromaCost;
	}

	public ItemStack getResult() {
		return result;
	}

	@Override
	public boolean matches(KhromaFabricationInput input, Level level) {
		if (!pattern.matches(input))
			return false;

		return input.getKhroma(khroma) != Khroma.EMPTY;
	}

	@Override
	public ItemStack assemble(KhromaFabricationInput input, Provider registries) {
		return result.copy();
	}

	public static final class Serializer implements RecipeSerializer<KhromaFabricationRecipe> {
		public static final MapCodec<KhromaFabricationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				KhromaFabricationRecipePattern.MAP_CODEC.fieldOf("pattern").forGetter(KhromaFabricationRecipe::getPattern),
				Khroma.CODEC.fieldOf("khroma").forGetter(KhromaFabricationRecipe::getKhroma),
				Codec.FLOAT.fieldOf("khromaCost").forGetter(KhromaFabricationRecipe::getKhromaCost),
				ItemStack.STRICT_CODEC.fieldOf("resumt").forGetter(KhromaFabricationRecipe::getResult)).apply(instance, KhromaFabricationRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, KhromaFabricationRecipe> STREAM_CODEC = StreamCodec.composite(
				KhromaFabricationRecipePattern.STREAM_CODEC,
				KhromaFabricationRecipe::getPattern,
				Khroma.STREAM_CODEC,
				KhromaFabricationRecipe::getKhroma,
				ByteBufCodecs.FLOAT,
				KhromaFabricationRecipe::getKhromaCost,
				ItemStack.STREAM_CODEC,
				KhromaFabricationRecipe::getResult,
				KhromaFabricationRecipe::new);

		@Override
		public MapCodec<KhromaFabricationRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, KhromaFabricationRecipe> streamCodec() {
			return STREAM_CODEC;
		}

	}

	@Override
	public RecipeSerializer<? extends Recipe<KhromaFabricationInput>> getSerializer() {
		return RecipeReference.SERIALIZER_KHROMA_FABRICATION.get();
	}

	@Override
	public RecipeType<? extends Recipe<KhromaFabricationInput>> getType() {
		return RecipeReference.KHROMA_FABRICATION.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		if (this.placementInfo == null) {
			this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
		}

		return this.placementInfo;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeReference.CATEGORY_KHROMA_FABRICATION.get();
	}

}
