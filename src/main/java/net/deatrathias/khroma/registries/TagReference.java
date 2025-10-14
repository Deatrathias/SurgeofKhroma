package net.deatrathias.khroma.registries;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagReference {

	/**
	 * Block tags
	 */
	public static class Blocks {
		public static final TagKey<Block> KHROMETAL_BLOCKS = blockTag("khrometal_blocks");
		public static final TagKey<Block> KHROMA_DEVICES = blockTag("khroma_devices");
		public static final TagKey<Block> KHROMA_ASPECTED_RED = blockTag("khroma_aspected/red");
		public static final TagKey<Block> KHROMA_ASPECTED_GREEN = blockTag("khroma_aspected/green");
		public static final TagKey<Block> KHROMA_ASPECTED_BLUE = blockTag("khroma_aspected/blue");
		public static final TagKey<Block> KHROMA_ASPECTED_WHITE = blockTag("khroma_aspected/white");
		public static final TagKey<Block> KHROMA_ASPECTED_BLACK = blockTag("khroma_aspected/black");
		public static final TagKey<Block> IMBUED_TREE_LOGS = blockTag("imbued_tree_logs");
		public static final TagKey<Block> IMBUED_TREE_LEAVES = blockTag("imbued_tree_leaves");
		public static final TagKey<Block> IMBUED_TREE_SAPLINGS = blockTag("imbued_tree_sapling");
		public static final TagKey<Block> PILLARS = blockTag("pillars");
		public static final TagKey<Block> WOODEN_PILLARS = blockTag("wooden_pillars");
		public static final TagKey<Block> COLLECTOR_STRUCTURE_COMPONENTS = blockTag("collector_structure_components");

		public static final TagKey<Block> C_ORES_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/chromium"));
		public static final TagKey<Block> C_STORAGE_BLOCKS_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/chromium"));
		public static final TagKey<Block> C_STORAGE_BLOCKS_RAW_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_chromium"));
	}

	/**
	 * Item tags
	 */
	public static class Items {
		public static final TagKey<Item> KHROMA_ASPECTED_RED = itemTag("khroma_aspected/red");
		public static final TagKey<Item> KHROMA_ASPECTED_GREEN = itemTag("khroma_aspected/green");
		public static final TagKey<Item> KHROMA_ASPECTED_BLUE = itemTag("khroma_aspected/blue");
		public static final TagKey<Item> KHROMA_ASPECTED_WHITE = itemTag("khroma_aspected/white");
		public static final TagKey<Item> KHROMA_ASPECTED_BLACK = itemTag("khroma_aspected/black");
		public static final TagKey<Item> KHROMETAL_INGOT_RED = itemTag("ingots/khrometal/red");
		public static final TagKey<Item> KHROMETAL_INGOT_GREEN = itemTag("ingots/khrometal/green");
		public static final TagKey<Item> KHROMETAL_INGOT_BLUE = itemTag("ingots/khrometal/blue");
		public static final TagKey<Item> KHROMETAL_INGOT_WHITE = itemTag("ingots/khrometal/white");
		public static final TagKey<Item> KHROMETAL_INGOT_BLACK = itemTag("ingots/khrometal/black");
		public static final TagKey<Item> KHROMETAL_INGOTS = itemTag("ingots/khrometal");
		public static final TagKey<Item> BASE_NUGGET = itemTag("base_nugget");
		public static final TagKey<Item> BASE_INGOT = itemTag("base_ingot");
		public static final TagKey<Item> KHROMETAL_SWORDS = itemTag("khrometal_swords");
		public static final TagKey<Item> KHROMETAL_PICKAXES = itemTag("khrometal_pickaxes");
		public static final TagKey<Item> KHROMETAL_AXES = itemTag("khrometal_axes");
		public static final TagKey<Item> KHROMETAL_SHOVELS = itemTag("khrometal_shovels");
		public static final TagKey<Item> KHROMETAL_TOOLS = itemTag("khrometal_tools");
		public static final TagKey<Item> CONVERTS_TO_DYE_RED_SINGULAR = itemTag("converts_to_dye/red/singular");
		public static final TagKey<Item> CONVERTS_TO_DYE_RED_HALF = itemTag("converts_to_dye/red/half");
		public static final TagKey<Item> CONVERTS_TO_DYE_RED_DOUBLE = itemTag("converts_to_dye/red/double");
		public static final TagKey<Item> CONVERTS_TO_DYE_GREEN_SINGULAR = itemTag("converts_to_dye/green/singular");
		public static final TagKey<Item> CONVERTS_TO_DYE_GREEN_HALF = itemTag("converts_to_dye/green/half");
		public static final TagKey<Item> CONVERTS_TO_DYE_GREEN_DOUBLE = itemTag("converts_to_dye/green/double");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLUE_SINGULAR = itemTag("converts_to_dye/blue/singular");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLUE_HALF = itemTag("converts_to_dye/blue/half");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLUE_DOUBLE = itemTag("converts_to_dye/blue/double");
		public static final TagKey<Item> CONVERTS_TO_DYE_WHITE_SINGULAR = itemTag("converts_to_dye/white/singular");
		public static final TagKey<Item> CONVERTS_TO_DYE_WHITE_HALF = itemTag("converts_to_dye/white/half");
		public static final TagKey<Item> CONVERTS_TO_DYE_WHITE_DOUBLE = itemTag("converts_to_dye/white/double");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLACK_SINGULAR = itemTag("converts_to_dye/black/singular");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLACK_HALF = itemTag("converts_to_dye/black/half");
		public static final TagKey<Item> CONVERTS_TO_DYE_BLACK_DOUBLE = itemTag("converts_to_dye/black/double");
		public static final TagKey<Item> REACTANTS_COMMON_RED = itemTag("reactants/common/red");
		public static final TagKey<Item> REACTANTS_COMMON_GREEN = itemTag("reactants/common/green");
		public static final TagKey<Item> REACTANTS_COMMON_BLUE = itemTag("reactants/common/blue");
		public static final TagKey<Item> REACTANTS_COMMON_WHITE = itemTag("reactants/common/white");
		public static final TagKey<Item> REACTANTS_COMMON_BLACK = itemTag("reactants/common/black");
		public static final TagKey<Item> REACTANTS_UNCOMMON_RED = itemTag("reactants/uncommon/red");
		public static final TagKey<Item> REACTANTS_UNCOMMON_GREEN = itemTag("reactants/uncommon/green");
		public static final TagKey<Item> REACTANTS_UNCOMMON_BLUE = itemTag("reactants/uncommon/blue");
		public static final TagKey<Item> REACTANTS_UNCOMMON_WHITE = itemTag("reactants/uncommon/white");
		public static final TagKey<Item> REACTANTS_UNCOMMON_BLACK = itemTag("reactants/uncommon/black");
		public static final TagKey<Item> REACTANTS_RARE_RED = itemTag("reactants/rare/red");
		public static final TagKey<Item> REACTANTS_RARE_GREEN = itemTag("reactants/rare/green");
		public static final TagKey<Item> REACTANTS_RARE_BLUE = itemTag("reactants/rare/blue");
		public static final TagKey<Item> REACTANTS_RARE_WHITE = itemTag("reactants/rare/white");
		public static final TagKey<Item> REACTANTS_RARE_BLACK = itemTag("reactants/rare/black");

		public static final TagKey<Item> C_INGOTS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/chromium"));
		public static final TagKey<Item> C_NUGGETS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/chromium"));
		public static final TagKey<Item> C_RAW_MATERIALS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/chromium"));

		public static final TagKey<Item> KHROMETAL_BLOCKS = itemTag(Blocks.KHROMETAL_BLOCKS);
		public static final TagKey<Item> KHROMA_DEVICES = itemTag(Blocks.KHROMA_DEVICES);
		public static final TagKey<Item> IMBUED_TREE_LOGS = itemTag(Blocks.IMBUED_TREE_LOGS);
		public static final TagKey<Item> IMBUED_TREE_LEAVES = itemTag(Blocks.IMBUED_TREE_LEAVES);
		public static final TagKey<Item> IMBUED_TREE_SAPLINGS = itemTag(Blocks.IMBUED_TREE_SAPLINGS);
		public static final TagKey<Item> PILLARS = itemTag(Blocks.PILLARS);
		public static final TagKey<Item> WOODEN_PILLARS = itemTag(Blocks.WOODEN_PILLARS);
		public static final TagKey<Item> C_ORES_CHROMIUM = itemTag(Blocks.C_ORES_CHROMIUM);

		public static final TagKey<Item> C_STORAGE_BLOCKS_CHROMIUM = itemTag(Blocks.C_STORAGE_BLOCKS_CHROMIUM);
		public static final TagKey<Item> C_STORAGE_BLOCKS_RAW_CHROMIUM = itemTag(Blocks.C_STORAGE_BLOCKS_RAW_CHROMIUM);
	}

	/**
	 * Entity tags
	 */
	public static class Entities {
		public static final TagKey<EntityType<?>> SENSITIVE_TO_FEATHERCLIP = TagKey.create(Registries.ENTITY_TYPE, SurgeofKhroma.resource("sensitive_to_featherclip"));

		public static final TagKey<EntityType<?>> C_BIRDS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "birds"));
	}

	private static TagKey<Block> blockTag(String path) {
		return BlockTags.create(SurgeofKhroma.resource(path));
	}

	private static TagKey<Item> itemTag(String path) {
		return ItemTags.create(SurgeofKhroma.resource(path));
	}

	private static TagKey<Item> itemTag(TagKey<Block> blockTag) {
		return ItemTags.create(blockTag.location());
	}
}
