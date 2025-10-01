package net.deatrathias.khroma.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

public class KhromaFabricationRecipePattern {
	private final List<Optional<Ingredient>> ingredients;
	private final Optional<Data> data;

	public List<Optional<Ingredient>> ingredients() {
		return ingredients;
	}

	public static final MapCodec<KhromaFabricationRecipePattern> MAP_CODEC = Data.MAP_CODEC.flatXmap(KhromaFabricationRecipePattern::unpack,
			pattern -> pattern.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));

	public static final StreamCodec<RegistryFriendlyByteBuf, KhromaFabricationRecipePattern> STREAM_CODEC = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list())
			.map(KhromaFabricationRecipePattern::createFromNetwork, pattern -> pattern.ingredients);

	private KhromaFabricationRecipePattern(List<Optional<Ingredient>> ingredients, Optional<Data> data) {
		this.ingredients = ingredients;
		this.data = data;
	}

	private static KhromaFabricationRecipePattern createFromNetwork(List<Optional<Ingredient>> ingredients) {
		return new KhromaFabricationRecipePattern(ingredients, Optional.empty());
	}

	public static KhromaFabricationRecipePattern of(Map<Character, Ingredient> key, List<String> pattern) {
		Data data = new Data(key, pattern);
		return unpack(data).getOrThrow();
	}

	private static DataResult<KhromaFabricationRecipePattern> unpack(Data data) {
		List<Optional<Ingredient>> ingredients = new ArrayList<Optional<Ingredient>>(8);
		CharSet keySet = new CharArraySet(data.key.keySet());

		int lineCount = 0;
		for (String line : data.pattern) {
			for (int i = 0; i < line.length(); i++) {
				char key = line.charAt(i);
				if (key == ' ')
					ingredients.add(Optional.empty());
				else if (i != 1 || lineCount != 1) {
					Ingredient ingredient = data.key.get(key);
					if (ingredient == null)
						return DataResult.error(() -> "Pattern references symbol '" + key + "' but it's not defined in the key");
					ingredients.add(Optional.of(ingredient));
					keySet.remove(key);
				}
			}

			lineCount++;
		}

		return keySet.isEmpty() ? DataResult.success(new KhromaFabricationRecipePattern(ingredients, Optional.of(data)))
				: DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + keySet);
	}

	public boolean matches(KhromaFabricationInput input) {
		if (input.size() != 8)
			return false;

		for (int i = 0; i < ingredients.size(); i++) {
			if (!Ingredient.testOptionalIngredient(ingredients.get(i), input.getItem(i)))
				return false;
		}

		return true;
	}

	public static record Data(Map<Character, Ingredient> key, List<String> pattern) {
		private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf(3, 3).comapFlatMap(strings -> {
			for (String line : strings) {
				if (line.length() != 3)
					return DataResult.error(() -> "Invalid Pattern: lines must be 3 characters long.");
			}
			if (strings.get(1).charAt(1) != 'X')
				return DataResult.error(() -> "Invalid Pattern: center must be X.");
			return DataResult.success(strings);
		}, Function.identity());

		private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(key -> {
			if (key.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '" + key + "' is an invalid symbol (must be 1 character only).");
			} else {
				if (" ".equals(key))
					return DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.");
				else if ("X".equals(key))
					return DataResult.error(() -> "Invalid key entry: 'X' is a reserved symbol.");

				return DataResult.success(key.charAt(0));
			}
		}, String::valueOf);

		public static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec(
				p_360068_ -> p_360068_.group(
						ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC).fieldOf("key").forGetter(Data::key),
						PATTERN_CODEC.fieldOf("pattern").forGetter(Data::pattern))
						.apply(p_360068_, Data::new));
	}
}
