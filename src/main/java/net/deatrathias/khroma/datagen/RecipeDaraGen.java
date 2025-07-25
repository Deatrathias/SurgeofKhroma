package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.TagReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.recipes.CraftingSpannerRecipe;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public class RecipeDaraGen extends ModdedRecipeProvider {

	protected RecipeDaraGen(Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected String getModId() {
		return SurgeofKhroma.MODID;
	}

	@Override
	protected void buildRecipes() {
		var itemRegistry = registries.lookupOrThrow(Registries.ITEM);

		chromiumRecipes();
		for (String khromaName : Khroma.KhromaNames) {
			recipesPerKhroma(itemRegistry, khromaName);
		}
		khromaItemsRecipes();
		khromaDevicesRecipes();
	}

	private void chromiumRecipes() {
		oreSmelting(List.of(RegistryReference.ITEM_BLOCK_CHROMIUM_ORE, RegistryReference.ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE, RegistryReference.ITEM_RAW_CHROMIUM), RecipeCategory.MISC,
				RegistryReference.ITEM_CHROMIUM_INGOT, 0.7f, 200, getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		oreBlasting(List.of(RegistryReference.ITEM_BLOCK_CHROMIUM_ORE, RegistryReference.ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE, RegistryReference.ITEM_RAW_CHROMIUM), RecipeCategory.MISC,
				RegistryReference.ITEM_CHROMIUM_INGOT, 0.7f, 100, getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_INGOT, RecipeCategory.BUILDING_BLOCKS,
				RegistryReference.ITEM_BLOCK_CHROMIUM_BLOCK, getModdedConversionRecipeName(RegistryReference.ITEM_CHROMIUM_INGOT, RegistryReference.ITEM_BLOCK_CHROMIUM_BLOCK),
				getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipesWithCustomPacking(RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_NUGGET, RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_INGOT,
				getModdedRecipeName(RegistryReference.ITEM_CHROMIUM_INGOT) + "_from_nuggets", getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipes(RecipeCategory.MISC, RegistryReference.ITEM_RAW_CHROMIUM, RecipeCategory.BUILDING_BLOCKS, RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK);
	}

	/**
	 * Generate recipes that are identical for each khroma base color
	 * 
	 * @param recipeOutput
	 * @param itemRegistry
	 * @param khromaName
	 */
	private void recipesPerKhroma(RegistryLookup<Item> itemRegistry, String khromaName) {
		Khroma khroma = Khroma.fromName(khromaName);
		var khrometalIngotLocation = SurgeofKhroma.resource("khrometal_ingot_" + khromaName);
		var khrometalIngot = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, khrometalIngotLocation)).value();

		output.accept(ResourceKey.create(Registries.RECIPE, khrometalIngotLocation),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(TagReference.ITEM_BASE_INGOT)), khroma, new ItemStack(khrometalIngot), 5000), null);

		var khrometalBlock = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_block_" + khromaName))).value();

		nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, khrometalIngot, RecipeCategory.BUILDING_BLOCKS, khrometalBlock,
				getModdedConversionRecipeName(khrometalIngot, khrometalBlock), getItemName(khrometalIngot));

		var dye = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(khromaName + "_dye")));

		output.accept(ResourceKey.create(Registries.RECIPE, SurgeofKhroma.resource(khromaName + "_dye_imbuement")),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/singular")))), khroma, new ItemStack(dye, 2),
						2500),
				null);
		output.accept(ResourceKey.create(Registries.RECIPE, SurgeofKhroma.resource(khromaName + "_dye_imbuement_double")),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/double")))), khroma, new ItemStack(dye, 4),
						5000),
				null);
		output.accept(ResourceKey.create(Registries.RECIPE, SurgeofKhroma.resource(khromaName + "_dye_imbuement_half")),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/half")))), khroma, new ItemStack(dye, 1),
						1250),
				null);

		var sword = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_sword"))).map(ref -> ref.value()).orElse(null);
		var pickaxe = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_pickaxe"))).map(ref -> ref.value()).orElse(null);
		var axe = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_axe"))).map(ref -> ref.value()).orElse(null);
		var shovel = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_shovel"))).map(ref -> ref.value()).orElse(null);

		generateTools(TagKey.create(Registries.ITEM, SurgeofKhroma.resource("ingots/khrometal/" + khromaName)), sword, pickaxe, axe, shovel);
	}

	private void generateTools(TagKey<Item> material, ItemLike sword, ItemLike pickaxe, ItemLike axe, ItemLike shovel) {
		if (sword != null)
			shaped(RecipeCategory.COMBAT, sword)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("X")
					.pattern("X")
					.pattern("#")
					.unlockedBy("has_item", has(material))
					.save(output);
		if (pickaxe != null)
			shaped(RecipeCategory.TOOLS, pickaxe)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("XXX")
					.pattern(" # ")
					.pattern(" # ")
					.unlockedBy("has_item", has(material))
					.save(output);
		if (axe != null)
			shaped(RecipeCategory.TOOLS, axe)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("XX")
					.pattern("X#")
					.pattern(" #")
					.unlockedBy("has_item", has(material))
					.save(output);
		if (shovel != null)
			shaped(RecipeCategory.TOOLS, shovel)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("X")
					.pattern("#")
					.pattern("#")
					.unlockedBy("has_item", has(material))
					.save(output);
	}

	private void khromaItemsRecipes() {
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('r', Tags.Items.DYES_RED)
				.define('g', Tags.Items.DYES_GREEN)
				.define('u', Tags.Items.DYES_BLUE)
				.define('w', Tags.Items.DYES_WHITE)
				.define('b', Tags.Items.DYES_BLACK)
				.define('#', TagReference.ITEM_BASE_NUGGET)
				.pattern("w#b")
				.pattern("rgu")
				.unlockedBy("has_nugget", has(TagReference.ITEM_BASE_NUGGET))
				.save(output);

		shaped(RecipeCategory.MISC, RegistryReference.ITEM_CHROMATIC_GLASSES)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.pattern("gcg")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(output);

		shaped(RecipeCategory.MISC, Items.GREEN_DYE)
				.define('#', Items.KELP)
				.pattern("##")
				.pattern("##")
				.group(getItemName(Items.GREEN_DYE))
				.unlockedBy("has_kelp", has(Items.KELP))
				.save(output, getModdedConversionRecipeName(Items.GREEN_DYE, Items.KELP));

		RecipeOutputWrapper<ShapedRecipe, CraftingSpannerRecipe> spannerOutput = new RecipeOutputWrapper<ShapedRecipe, CraftingSpannerRecipe>(output, CraftingSpannerRecipe::new);
		shaped(RecipeCategory.TOOLS, RegistryReference.ITEM_KHROMETAL_SPANNER)
				.define('#', TagReference.ITEM_KHROMETAL_INGOTS)
				.pattern("# #")
				.pattern(" # ")
				.pattern(" # ")
				.group(getItemName(RegistryReference.ITEM_KHROMETAL_SPANNER))
				.unlockedBy("has_khrometal", has(TagReference.ITEM_KHROMETAL_INGOTS))
				.save(spannerOutput);
	}

	private void khromaDevicesRecipes() {
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_LINE, 8)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern(" g ")
				.pattern("#c#")
				.pattern(" g ")
				.group(getItemName(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_LINE, 8)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.ITEM_KHROMETAL_INGOTS)
				.pattern(" g ")
				.pattern("# #")
				.pattern(" g ")
				.group(getItemName(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.unlockedBy("has_khrometal", has(TagReference.ITEM_KHROMETAL_INGOTS))
				.save(output, getModdedRecipeName(RegistryReference.ITEM_BLOCK_KHROMA_LINE) + "_from_khrometal");

		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_APERTURE)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.LEVER)
				.pattern("#Xo")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_COMBINER)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern("#X#")
				.pattern(" o ")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern(" # ")
				.pattern("oXo")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_DISSIPATOR)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.IRON_BARS)
				.pattern("##X")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_NODE_COLLECTOR)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern("###")
				.pattern("#c#")
				.pattern("###")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(output);
		shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_IMBUER)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern("###")
				.pattern("XcX")
				.pattern("###")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(output);

	}

	public static class Runner extends RecipeProvider.Runner {

		protected Runner(PackOutput packOutput, CompletableFuture<Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(Provider registries, RecipeOutput output) {
			return new RecipeDaraGen(registries, output);
		}

		@Override
		public String getName() {
			return SurgeofKhroma.MODID + " recipes";
		}
	}
}
