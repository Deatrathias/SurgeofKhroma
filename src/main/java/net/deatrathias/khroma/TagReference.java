package net.deatrathias.khroma;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagReference {

	/**
	 * Block tags
	 */
	public static final TagKey<Block> BLOCK_KHROMETAL_BLOCKS = blockTagB("khrometal_blocks");
	public static final TagKey<Block> BLOCK_KHROMA_DEVICES = blockTagB("khroma_devices");
	public static final TagKey<Block> C_BLOCK_ORES_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/chromium"));
	public static final TagKey<Block> C_BLOCK_STORAGE_BLOCKS_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/chromium"));
	public static final TagKey<Block> C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_chromium"));

	/**
	 * Item tags
	 */
	public static final TagKey<Item> ITEM_KHROMETAL_INGOT_RED = itemTag("ingots/khrometal/red");
	public static final TagKey<Item> ITEM_KHROMETAL_INGOT_GREEN = itemTag("ingots/khrometal/green");
	public static final TagKey<Item> ITEM_KHROMETAL_INGOT_BLUE = itemTag("ingots/khrometal/blue");
	public static final TagKey<Item> ITEM_KHROMETAL_INGOT_WHITE = itemTag("ingots/khrometal/white");
	public static final TagKey<Item> ITEM_KHROMETAL_INGOT_BLACK = itemTag("ingots/khrometal/black");
	public static final TagKey<Item> ITEM_KHROMETAL_INGOTS = itemTag("ingots/khrometal");
	public static final TagKey<Item> ITEM_BASE_NUGGET = itemTag("base_nugget");
	public static final TagKey<Item> ITEM_BASE_INGOT = itemTag("base_ingot");
	public static final TagKey<Item> ITEM_KHROMETAL_SWORDS = itemTag("khrometal_swords");
	public static final TagKey<Item> ITEM_KHROMETAL_PICKAXES = itemTag("khrometal_pickaxes");
	public static final TagKey<Item> ITEM_KHROMETAL_AXES = itemTag("khrometal_axes");
	public static final TagKey<Item> ITEM_KHROMETAL_SHOVELS = itemTag("khrometal_shovels");
	public static final TagKey<Item> ITEM_KHROMETAL_TOOLS = itemTag("khrometal_tools");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_RED_SINGULAR = itemTag("converts_to_dye/red/singular");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_RED_HALF = itemTag("converts_to_dye/red/half");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_RED_DOUBLE = itemTag("converts_to_dye/red/double");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_GREEN_SINGULAR = itemTag("converts_to_dye/green/singular");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_GREEN_HALF = itemTag("converts_to_dye/green/half");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_GREEN_DOUBLE = itemTag("converts_to_dye/green/double");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLUE_SINGULAR = itemTag("converts_to_dye/blue/singular");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLUE_HALF = itemTag("converts_to_dye/blue/half");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLUE_DOUBLE = itemTag("converts_to_dye/blue/double");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_WHITE_SINGULAR = itemTag("converts_to_dye/white/singular");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_WHITE_HALF = itemTag("converts_to_dye/white/half");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_WHITE_DOUBLE = itemTag("converts_to_dye/white/double");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLACK_SINGULAR = itemTag("converts_to_dye/black/singular");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLACK_HALF = itemTag("converts_to_dye/black/half");
	public static final TagKey<Item> ITEM_CONVERTS_TO_DYE_BLACK_DOUBLE = itemTag("converts_to_dye/black/double");
	public static final TagKey<Item> C_ITEM_INGOTS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/chromium"));
	public static final TagKey<Item> C_ITEM_NUGGETS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/chromium"));
	public static final TagKey<Item> C_ITEM_RAW_MATERIALS_CHROMIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/chromium"));

	public static final TagKey<Item> ITEM_KHROMETAL_BLOCKS = itemTag(BLOCK_KHROMETAL_BLOCKS);
	public static final TagKey<Item> ITEM_KHROMA_DEVICES = itemTag(BLOCK_KHROMA_DEVICES);
	public static final TagKey<Item> C_ITEM_ORES_CHROMIUM = itemTag(C_BLOCK_ORES_CHROMIUM);
	public static final TagKey<Item> C_ITEM_STORAGE_BLOCKS_CHROMIUM = itemTag(C_BLOCK_STORAGE_BLOCKS_CHROMIUM);
	public static final TagKey<Item> C_ITEM_STORAGE_BLOCKS_RAW_CHROMIUM = itemTag(C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM);

	private static TagKey<Block> blockTagB(String path) {
		return BlockTags.create(SurgeofKhroma.resource(path));
	}

	private static TagKey<Item> itemTag(String path) {
		return ItemTags.create(SurgeofKhroma.resource(path));
	}

	private static TagKey<Item> itemTag(TagKey<Block> blockTag) {
		return ItemTags.create(blockTag.location());
	}
}
