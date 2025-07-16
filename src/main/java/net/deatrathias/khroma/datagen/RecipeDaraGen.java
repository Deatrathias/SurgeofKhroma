package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.TagReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public class RecipeDaraGen extends ModdedRecipeProvider {

	public RecipeDaraGen(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries);
	}

	@Override
	protected String getModId() {
		return SurgeofKhroma.MODID;
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
		var itemRegistry = holderLookup.lookupOrThrow(Registries.ITEM);

		chromiumRecipes(recipeOutput);
		for (String khromaName : Khroma.KhromaNames) {
			recipesPerKhroma(recipeOutput, itemRegistry, khromaName);
		}
		khromaItemsRecipes(recipeOutput);
		khromaDevicesRecipes(recipeOutput);
	}

	private void chromiumRecipes(RecipeOutput recipeOutput) {
		oreSmelting(recipeOutput, List.of(RegistryReference.ITEM_BLOCK_CHROMIUM_ORE, RegistryReference.ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE, RegistryReference.ITEM_RAW_CHROMIUM), RecipeCategory.MISC,
				RegistryReference.ITEM_CHROMIUM_INGOT, 0.7f, 200, getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		oreBlasting(recipeOutput, List.of(RegistryReference.ITEM_BLOCK_CHROMIUM_ORE, RegistryReference.ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE, RegistryReference.ITEM_RAW_CHROMIUM), RecipeCategory.MISC,
				RegistryReference.ITEM_CHROMIUM_INGOT, 0.7f, 100, getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipesRecipesWithCustomUnpacking(recipeOutput, RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_INGOT, RecipeCategory.BUILDING_BLOCKS,
				RegistryReference.ITEM_BLOCK_CHROMIUM_BLOCK, getConversionRecipeName(RegistryReference.ITEM_CHROMIUM_INGOT, RegistryReference.ITEM_BLOCK_CHROMIUM_BLOCK),
				getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_NUGGET, RecipeCategory.MISC, RegistryReference.ITEM_CHROMIUM_INGOT,
				getItemName(RegistryReference.ITEM_CHROMIUM_INGOT) + "_from_nuggets", getItemName(RegistryReference.ITEM_CHROMIUM_INGOT));
		nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, RegistryReference.ITEM_RAW_CHROMIUM, RecipeCategory.BUILDING_BLOCKS, RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK);
	}

	/**
	 * Generate recipes that are identical for each khroma base color
	 * 
	 * @param recipeOutput
	 * @param itemRegistry
	 * @param khromaName
	 */
	private void recipesPerKhroma(RecipeOutput recipeOutput, RegistryLookup<Item> itemRegistry, String khromaName) {
		Khroma khroma = Khroma.fromName(khromaName);
		var khrometalIngotLocation = SurgeofKhroma.resource("khrometal_ingot_" + khromaName);
		var khrometalIngot = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, khrometalIngotLocation)).value();
		recipeOutput.accept(khrometalIngotLocation, new KhromaImbuementRecipe(Ingredient.of(TagReference.ITEM_BASE_INGOT), khroma, new ItemStack(khrometalIngot), 5000), null);

		var khrometalBlock = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_block_" + khromaName))).value();

		nineBlockStorageRecipesRecipesWithCustomUnpacking(recipeOutput, RecipeCategory.MISC, khrometalIngot, RecipeCategory.BUILDING_BLOCKS, khrometalBlock,
				getConversionRecipeName(khrometalIngot, khrometalBlock), getItemName(khrometalIngot));

		var dye = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(khromaName + "_dye")));

		recipeOutput.accept(SurgeofKhroma.resource(khromaName + "_dye_imbuement"),
				new KhromaImbuementRecipe(Ingredient.of(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/singular"))), khroma, new ItemStack(dye, 2), 2500), null);
		recipeOutput.accept(SurgeofKhroma.resource(khromaName + "_dye_imbuement_double"),
				new KhromaImbuementRecipe(Ingredient.of(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/double"))), khroma, new ItemStack(dye, 4), 5000), null);
		recipeOutput.accept(SurgeofKhroma.resource(khromaName + "_dye_imbuement_half"),
				new KhromaImbuementRecipe(Ingredient.of(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/half"))), khroma, new ItemStack(dye, 1), 1250), null);

		var sword = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_sword"))).map(ref -> ref.value()).orElse(null);
		var pickaxe = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_pickaxe"))).map(ref -> ref.value()).orElse(null);
		var axe = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_axe"))).map(ref -> ref.value()).orElse(null);
		var shovel = itemRegistry.get(ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_" + khromaName + "_shovel"))).map(ref -> ref.value()).orElse(null);

		generateTools(recipeOutput, TagKey.create(Registries.ITEM, SurgeofKhroma.resource("ingots/khrometal/" + khromaName)), sword, pickaxe, axe, shovel);
	}

	private void generateTools(RecipeOutput recipeOutput, TagKey<Item> material, ItemLike sword, ItemLike pickaxe, ItemLike axe, ItemLike shovel) {
		if (sword != null)
			ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("X")
					.pattern("X")
					.pattern("#")
					.unlockedBy("has_item", has(material))
					.save(recipeOutput);
		if (pickaxe != null)
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("XXX")
					.pattern(" # ")
					.pattern(" # ")
					.unlockedBy("has_item", has(material))
					.save(recipeOutput);
		if (axe != null)
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("XX")
					.pattern("X#")
					.pattern(" #")
					.unlockedBy("has_item", has(material))
					.save(recipeOutput);
		if (shovel != null)
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel)
					.define('#', Items.STICK)
					.define('X', material)
					.pattern("X")
					.pattern("#")
					.pattern("#")
					.unlockedBy("has_item", has(material))
					.save(recipeOutput);
	}

	private void khromaItemsRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('r', Tags.Items.DYES_RED)
				.define('g', Tags.Items.DYES_GREEN)
				.define('u', Tags.Items.DYES_BLUE)
				.define('w', Tags.Items.DYES_WHITE)
				.define('b', Tags.Items.DYES_BLACK)
				.define('#', TagReference.ITEM_BASE_NUGGET)
				.pattern("w#b")
				.pattern("rgu")
				.unlockedBy("has_nugget", has(TagReference.ITEM_BASE_NUGGET))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_CHROMATIC_GLASSES)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.pattern("gcg")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GREEN_DYE)
				.define('#', Items.KELP)
				.pattern("##")
				.pattern("##")
				.group(getItemName(Items.GREEN_DYE))
				.unlockedBy("has_kelp", has(Items.KELP))
				.save(recipeOutput, getConversionRecipeName(Items.GREEN_DYE, Items.KELP));
	}

	private void khromaDevicesRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_LINE, 8)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern(" g ")
				.pattern("#c#")
				.pattern(" g ")
				.group(getItemName(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_LINE, 8)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.ITEM_KHROMETAL_INGOTS)
				.pattern(" g ")
				.pattern("# #")
				.pattern(" g ")
				.group(getItemName(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.unlockedBy("has_khrometal", has(TagReference.ITEM_KHROMETAL_INGOTS))
				.save(recipeOutput, getSimpleRecipeName(RegistryReference.ITEM_BLOCK_KHROMA_LINE) + "_from_khrometal");

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_APERTURE)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.LEVER)
				.pattern("#Xo")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_COMBINER)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern("#X#")
				.pattern(" o ")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR)
				.define('o', Items.DROPPER)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern(" # ")
				.pattern("oXo")
				.unlockedBy("has_line", has(RegistryReference.ITEM_BLOCK_KHROMA_LINE))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_NODE_COLLECTOR)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern("###")
				.pattern("#c#")
				.pattern("###")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryReference.ITEM_BLOCK_KHROMA_IMBUER)
				.define('c', RegistryReference.ITEM_CHROMATIC_NUCLEUS)
				.define('X', RegistryReference.ITEM_BLOCK_KHROMA_LINE)
				.define('#', TagReference.ITEM_BASE_INGOT)
				.pattern("###")
				.pattern("XcX")
				.pattern("###")
				.unlockedBy("has_nucleus", has(RegistryReference.ITEM_CHROMATIC_NUCLEUS))
				.save(recipeOutput);

	}
}
