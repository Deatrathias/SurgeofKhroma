package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.recipes.CraftingSpannerRecipe;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.ItemReference;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
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
		imbuedWoodRecipes();
		for (String khromaName : Khroma.KhromaNames) {
			recipesPerKhroma(itemRegistry, khromaName);
		}
		khromaItemsRecipes();
		khromaDevicesRecipes();

		khromaFabrication(Khroma.RED, 10000, ItemReference.KHROMETAL_INGOT_RED, 8)
				.define('#', TagReference.Items.BASE_INGOT)
				.define('$', Tags.Items.DYES_RED)
				.pattern(" $ ")
				.pattern("#X#")
				.pattern(" $ ")
				.unlockedBy("has_ingot", has(TagReference.Items.BASE_INGOT))
				.save(output, ResourceKey.create(Registries.RECIPE, ItemReference.KHROMETAL_INGOT_RED.getId().withSuffix("_fabrication")));
	}

	private void chromiumRecipes() {
		oreSmelting(List.of(BlockReference.CHROMIUM_ORE, BlockReference.DEEPSLATE_CHROMIUM_ORE, ItemReference.RAW_CHROMIUM), RecipeCategory.MISC,
				ItemReference.CHROMIUM_INGOT, 0.7f, 200, getItemName(ItemReference.CHROMIUM_INGOT));
		oreBlasting(List.of(BlockReference.CHROMIUM_ORE, BlockReference.DEEPSLATE_CHROMIUM_ORE, ItemReference.RAW_CHROMIUM), RecipeCategory.MISC,
				ItemReference.CHROMIUM_INGOT, 0.7f, 100, getItemName(ItemReference.CHROMIUM_INGOT));
		nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, ItemReference.CHROMIUM_INGOT, RecipeCategory.BUILDING_BLOCKS,
				BlockReference.CHROMIUM_BLOCK, getModdedConversionRecipeName(ItemReference.CHROMIUM_INGOT, BlockReference.CHROMIUM_BLOCK),
				getItemName(ItemReference.CHROMIUM_INGOT));
		nineBlockStorageRecipesWithCustomPacking(RecipeCategory.MISC, ItemReference.CHROMIUM_NUGGET, RecipeCategory.MISC, ItemReference.CHROMIUM_INGOT,
				getModdedRecipeName(ItemReference.CHROMIUM_INGOT) + "_from_nuggets", getItemName(ItemReference.CHROMIUM_INGOT));
		nineBlockStorageRecipes(RecipeCategory.MISC, ItemReference.RAW_CHROMIUM, RecipeCategory.BUILDING_BLOCKS, BlockReference.RAW_CHROMIUM_BLOCK);
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
		var khrometalIngotLocation = SurgeofKhroma.resource(khromaName + "_khrometal_ingot");
		var khrometalIngot = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, khrometalIngotLocation)).value();

		output.accept(ResourceKey.create(Registries.RECIPE, khrometalIngotLocation),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(TagReference.Items.BASE_INGOT)), khroma, new ItemStack(khrometalIngot), 5000), null);

		var khrometalBlock = itemRegistry.getOrThrow(SurgeofKhroma.resourceKey(Registries.ITEM, "khrometal_block_" + khromaName)).value();

		nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, khrometalIngot, RecipeCategory.BUILDING_BLOCKS, khrometalBlock,
				getModdedConversionRecipeName(khrometalIngot, khrometalBlock), getItemName(khrometalIngot));

		var dye = itemRegistry.getOrThrow(ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(khromaName + "_dye")));

		output.accept(SurgeofKhroma.resourceKey(Registries.RECIPE, khromaName + "_dye_imbuement"),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/singular")))), khroma, new ItemStack(dye, 2),
						2500),
				null);
		output.accept(SurgeofKhroma.resourceKey(Registries.RECIPE, khromaName + "_dye_imbuement_double"),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/double")))), khroma, new ItemStack(dye, 4),
						5000),
				null);
		output.accept(SurgeofKhroma.resourceKey(Registries.RECIPE, khromaName + "_dye_imbuement_half"),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.create(SurgeofKhroma.resource("converts_to_dye/" + khromaName + "/half")))), khroma, new ItemStack(dye, 1),
						1250),
				null);

		var sword = itemRegistry.get(SurgeofKhroma.resourceKey(Registries.ITEM, khromaName + "_khrometal_sword")).map(ref -> ref.value()).orElse(null);
		var pickaxe = itemRegistry.get(SurgeofKhroma.resourceKey(Registries.ITEM, khromaName + "_khrometal_pickaxe")).map(ref -> ref.value()).orElse(null);
		var axe = itemRegistry.get(SurgeofKhroma.resourceKey(Registries.ITEM, khromaName + "_khrometal_axe")).map(ref -> ref.value()).orElse(null);
		var shovel = itemRegistry.get(SurgeofKhroma.resourceKey(Registries.ITEM, khromaName + "_khrometal_shovel")).map(ref -> ref.value()).orElse(null);

		generateTools(ItemTags.create(SurgeofKhroma.resource("ingots/khrometal/" + khromaName)), sword, pickaxe, axe, shovel);

		Item sapling = null;

		if (khromaName.equals("red"))
			sapling = BlockReference.SPARKTREE.get(TreeBlock.SAPLING).asItem();
		else if (khromaName.equals("green"))
			sapling = BlockReference.BLOOMTREE.get(TreeBlock.SAPLING).asItem();
		else if (khromaName.equals("blue"))
			sapling = BlockReference.FLOWTREE.get(TreeBlock.SAPLING).asItem();
		else if (khromaName.equals("white"))
			sapling = BlockReference.SKYTREE.get(TreeBlock.SAPLING).asItem();
		else if (khromaName.equals("black"))
			sapling = BlockReference.GRIMTREE.get(TreeBlock.SAPLING).asItem();

		output.accept(SurgeofKhroma.resourceKey(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(sapling).getPath()),
				new KhromaImbuementRecipe(Ingredient.of(itemRegistry.getOrThrow(ItemTags.SAPLINGS)), khroma, new ItemStack(sapling, 1), 5000), null);

		khromaFabrication(khroma, 10000, khrometalIngot, 8)
				.define('#', TagReference.Items.BASE_INGOT)
				.define('$', ItemTags.create(SurgeofKhroma.resource("reactants/common/" + khromaName)))
				.pattern(" $ ")
				.pattern("#X#")
				.pattern(" $ ")
				.unlockedBy("has_ingot", has(TagReference.Items.BASE_INGOT))
				.save(output, ResourceKey.create(Registries.RECIPE, SurgeofKhroma.resource("khroma_ingot_" + khromaName + "_fabrication")));
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
		shaped(RecipeCategory.MISC, ItemReference.CHROMATIC_NUCLEUS)
				.define('r', Tags.Items.DYES_RED)
				.define('g', Tags.Items.DYES_GREEN)
				.define('u', Tags.Items.DYES_BLUE)
				.define('w', Tags.Items.DYES_WHITE)
				.define('b', Tags.Items.DYES_BLACK)
				.define('#', TagReference.Items.BASE_NUGGET)
				.pattern("w#b")
				.pattern("rgu")
				.unlockedBy("has_nugget", has(TagReference.Items.BASE_NUGGET))
				.save(output);

		shaped(RecipeCategory.MISC, ItemReference.CHROMATIC_GLASSES)
				.define('c', ItemReference.CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.pattern("gcg")
				.unlockedBy("has_nucleus", has(ItemReference.CHROMATIC_NUCLEUS))
				.save(output);

		shaped(RecipeCategory.MISC, Items.GREEN_DYE)
				.define('#', Items.KELP)
				.pattern("##")
				.pattern("##")
				.group(getItemName(Items.GREEN_DYE))
				.unlockedBy("has_kelp", has(Items.KELP))
				.save(output, getModdedConversionRecipeName(Items.GREEN_DYE, Items.KELP));

		RecipeOutputWrapper<ShapedRecipe, CraftingSpannerRecipe> spannerOutput = new RecipeOutputWrapper<ShapedRecipe, CraftingSpannerRecipe>(output, CraftingSpannerRecipe::new);
		shaped(RecipeCategory.TOOLS, ItemReference.KHROMETAL_SPANNER)
				.define('#', TagReference.Items.KHROMETAL_INGOTS)
				.pattern("# #")
				.pattern(" # ")
				.pattern(" # ")
				.group(getItemName(ItemReference.KHROMETAL_SPANNER))
				.unlockedBy("has_khrometal", has(TagReference.Items.KHROMETAL_INGOTS))
				.save(spannerOutput);

		shapeless(RecipeCategory.TOOLS, ItemReference.KHROMANCER_ARCHIVE)
				.requires(TagReference.Items.BASE_INGOT)
				.requires(ItemReference.CHROMATIC_NUCLEUS)
				.unlockedBy("has_ingot", has(TagReference.Items.BASE_INGOT))
				.save(output);

		shaped(RecipeCategory.TOOLS, ItemReference.WARP_CANISTER)
				.define('#', TagReference.Items.KHROMETAL_INGOT_BLACK)
				.define('X', Tags.Items.ENDER_PEARLS)
				.pattern("# #")
				.pattern("#X#")
				.pattern(" # ")
				.unlockedBy("has_black_khrometal", has(TagReference.Items.KHROMETAL_INGOT_BLACK))
				.save(output);

		output.accept(SurgeofKhroma.resourceKey(Registries.RECIPE, ItemReference.WARP_CANISTER.getId().getPath() + "_reset"),
				new ShapelessRecipe("", CraftingBookCategory.EQUIPMENT, new ItemStack(ItemReference.WARP_CANISTER.asItem(), 1), List.of(Ingredient.of(ItemReference.WARP_CANISTER))), null);

		shaped(RecipeCategory.TOOLS, ItemReference.FEATHERED_BOOTS)
				.define('#', Tags.Items.FEATHERS)
				.define('X', TagReference.Items.KHROMETAL_INGOT_WHITE)
				.define('L', Tags.Items.LEATHERS)
				.define('~', Tags.Items.STRINGS)
				.pattern("# #")
				.pattern("X~X")
				.pattern("LLL")
				.unlockedBy("has_white_khrometal", has(TagReference.Items.KHROMETAL_INGOT_WHITE))
				.save(output);

		shaped(RecipeCategory.TOOLS, ItemReference.ANKLETS_OF_MOTION)
				.define('#', Tags.Items.INGOTS_GOLD)
				.define('X', TagReference.Items.KHROMETAL_INGOT_BLUE)
				.pattern(" X ")
				.pattern("# #")
				.pattern(" X ")
				.unlockedBy("has_blue_khrometal", has(TagReference.Items.KHROMETAL_INGOT_BLUE))
				.save(output);
	}

	private void imbuedWoodRecipes() {
		for (var tree : BlockReference.IMBUED_TREES) {
			woodFromLogs(tree.get(TreeBlock.WOOD), tree.get(TreeBlock.LOG));
			woodFromLogs(tree.get(TreeBlock.STRIPPED_WOOD), tree.get(TreeBlock.STRIPPED_LOG));
			planksFromLogs(tree.get(TreeBlock.PLANKS), tree.getItemLogsTag(), 4);
			tree.ifPresent(TreeBlock.HANGING_SIGN, block -> hangingSign(block, tree.get(TreeBlock.STRIPPED_LOG)));
			generateRecipes(tree.getFamily(), FeatureFlags.VANILLA_SET);
			if (tree.getBoatItem() != null) {
				woodenBoat(tree.getBoatItem(), tree.get(TreeBlock.PLANKS));
				if (tree.getChestBoatItem() != null)
					chestBoat(tree.getChestBoatItem(), tree.getBoatItem());
			}

			shaped(RecipeCategory.BUILDING_BLOCKS, tree.get(TreeBlock.PILLAR), 2)
					.define('#', tree.get(TreeBlock.PLANKS))
					.define('X', tree.get(TreeBlock.SLAB))
					.pattern("X")
					.pattern("#")
					.pattern("X")
					.unlockedBy("has_slab", has(tree.get(TreeBlock.SLAB)))
					.save(output);
		}
	}

	private void khromaDevicesRecipes() {
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_LINE, 8)
				.define('c', ItemReference.CHROMATIC_NUCLEUS)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.Items.BASE_INGOT)
				.pattern(" g ")
				.pattern("#c#")
				.pattern(" g ")
				.group(getItemName(BlockReference.KHROMA_LINE))
				.unlockedBy("has_nucleus", has(ItemReference.CHROMATIC_NUCLEUS))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_LINE, 8)
				.define('g', Tags.Items.GLASS_PANES)
				.define('#', TagReference.Items.KHROMETAL_INGOTS)
				.pattern(" g ")
				.pattern("# #")
				.pattern(" g ")
				.group(getItemName(BlockReference.KHROMA_LINE))
				.unlockedBy("has_khrometal", has(TagReference.Items.KHROMETAL_INGOTS))
				.save(output, getModdedRecipeName(BlockReference.KHROMA_LINE) + "_from_khrometal");

		shaped(RecipeCategory.MISC, BlockReference.KHROMA_APERTURE)
				.define('o', Items.DROPPER)
				.define('X', BlockReference.KHROMA_LINE)
				.define('#', Items.LEVER)
				.pattern("#Xo")
				.unlockedBy("has_line", has(BlockReference.KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_COMBINER)
				.define('o', Items.DROPPER)
				.define('X', BlockReference.KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern("#X#")
				.pattern(" o ")
				.unlockedBy("has_line", has(BlockReference.KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_SEPARATOR)
				.define('o', Items.DROPPER)
				.define('X', BlockReference.KHROMA_LINE)
				.define('#', Items.PISTON)
				.pattern(" # ")
				.pattern("oXo")
				.unlockedBy("has_line", has(BlockReference.KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_DISSIPATOR)
				.define('X', BlockReference.KHROMA_LINE)
				.define('#', Items.IRON_BARS)
				.pattern("##X")
				.unlockedBy("has_line", has(BlockReference.KHROMA_LINE))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.NODE_COLLECTOR)
				.define('c', ItemReference.CHROMATIC_NUCLEUS)
				.define('#', TagReference.Items.BASE_INGOT)
				.pattern("###")
				.pattern("#c#")
				.pattern("###")
				.unlockedBy("has_nucleus", has(ItemReference.CHROMATIC_NUCLEUS))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.KHROMA_IMBUER)
				.define('c', ItemReference.CHROMATIC_NUCLEUS)
				.define('X', BlockReference.KHROMA_LINE)
				.define('#', TagReference.Items.BASE_INGOT)
				.pattern("###")
				.pattern("XcX")
				.pattern("###")
				.unlockedBy("has_nucleus", has(ItemReference.CHROMATIC_NUCLEUS))
				.save(output);
		shaped(RecipeCategory.MISC, BlockReference.ITEM_PEDESTAL)
				.define('#', TagReference.Items.KHROMETAL_INGOTS)
				.define('X', Items.STONE_SLAB)
				.pattern("# ")
				.pattern("#X")
				.pattern("##")
				.unlockedBy("has_khrometal", has(TagReference.Items.KHROMETAL_INGOTS))
				.save(output);

	}

	protected KhromaFabricationRecipeBuilder khromaFabrication(Khroma khroma, float khromaCost, ItemLike item, int count) {
		return new KhromaFabricationRecipeBuilder(items, khroma, khromaCost, item.asItem(), count);
	}

	protected KhromaFabricationRecipeBuilder khromaFabrication(Khroma khroma, float khromaCost, ItemLike item) {
		return new KhromaFabricationRecipeBuilder(items, khroma, khromaCost, item.asItem());
	}

	protected KhromaFabricationRecipeBuilder khromaFabrication(Khroma khroma, float khromaCost, ItemStack itemStack) {
		return new KhromaFabricationRecipeBuilder(items, khroma, khromaCost, itemStack.getItem(), itemStack.getCount());
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
