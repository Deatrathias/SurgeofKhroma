package net.deatrathias.khroma.datagen;

import java.util.List;

import net.deatrathias.khroma.RegistryReference;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class DataGenDefinitions {
	public static final List<DeferredBlock<Block>> cubeBlocks = List.of(RegistryReference.BLOCK_CHROMIUM_ORE, RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE, RegistryReference.BLOCK_CHROMIUM_BLOCK,
			RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK, RegistryReference.BLOCK_KHROMETAL_BLOCK_RED, RegistryReference.BLOCK_KHROMETAL_BLOCK_GREEN, RegistryReference.BLOCK_KHROMETAL_BLOCK_BLUE,
			RegistryReference.BLOCK_KHROMETAL_BLOCK_WHITE, RegistryReference.BLOCK_KHROMETAL_BLOCK_BLACK);

	public static final List<DeferredBlock<Block>> simpleBlocks = List.of(RegistryReference.BLOCK_NODE_COLLECTOR);

	public static final List<DeferredBlock<Block>> horDirectionBlocks = List.of(RegistryReference.BLOCK_KHROMA_PROVIDER, RegistryReference.BLOCK_KHROMA_MACHINE,
			RegistryReference.BLOCK_KHROMA_COMBINER, RegistryReference.BLOCK_KHROMA_SEPARATOR,
			RegistryReference.BLOCK_KHROMA_IMBUER);

	public static final List<DeferredBlock<Block>> fullDirectionBlocks = List.of(RegistryReference.BLOCK_KHROMA_APERTURE);

	public static final List<DeferredItem<BlockItem>> simpleItemBlocks = List.of(RegistryReference.ITEM_BLOCK_CHROMIUM_ORE, RegistryReference.ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE,
			RegistryReference.ITEM_BLOCK_CHROMIUM_BLOCK, RegistryReference.ITEM_BLOCK_RAW_CHROMIUM_BLOCK, RegistryReference.ITEM_BLOCK_KHROMETAL_BLOCK_RED,
			RegistryReference.ITEM_BLOCK_KHROMETAL_BLOCK_GREEN, RegistryReference.ITEM_BLOCK_KHROMETAL_BLOCK_BLUE, RegistryReference.ITEM_BLOCK_KHROMETAL_BLOCK_WHITE,
			RegistryReference.ITEM_BLOCK_KHROMETAL_BLOCK_BLACK, RegistryReference.ITEM_BLOCK_KHROMA_APERTURE, RegistryReference.ITEM_BLOCK_KHROMA_COMBINER, RegistryReference.ITEM_BLOCK_KHROMA_IMBUER,
			RegistryReference.ITEM_BLOCK_KHROMA_SEPARATOR, RegistryReference.ITEM_BLOCK_NODE_COLLECTOR);

	public static final List<DeferredBlock<Block>> dropsSelfBlocks = List.of(RegistryReference.BLOCK_CHROMIUM_BLOCK, RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK,
			RegistryReference.BLOCK_KHROMETAL_BLOCK_RED, RegistryReference.BLOCK_KHROMETAL_BLOCK_GREEN, RegistryReference.BLOCK_KHROMETAL_BLOCK_BLUE, RegistryReference.BLOCK_KHROMETAL_BLOCK_WHITE,
			RegistryReference.BLOCK_KHROMETAL_BLOCK_BLACK, RegistryReference.BLOCK_KHROMA_APERTURE, RegistryReference.BLOCK_KHROMA_COMBINER, RegistryReference.BLOCK_KHROMA_SEPARATOR,
			RegistryReference.BLOCK_KHROMA_PROVIDER, RegistryReference.BLOCK_KHROMA_MACHINE, RegistryReference.BLOCK_KHROMA_LINE, RegistryReference.BLOCK_NODE_COLLECTOR,
			RegistryReference.BLOCK_KHROMA_IMBUER);
	public static final List<DeferredBlock<Block>> dropsOreBlocks = List.of(RegistryReference.BLOCK_CHROMIUM_ORE, RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE);

	public static final List<DeferredItem<Item>> simpleItems = List.of(RegistryReference.ITEM_RAW_CHROMIUM, RegistryReference.ITEM_CHROMIUM_INGOT, RegistryReference.ITEM_CHROMIUM_NUGGET,
			RegistryReference.ITEM_KHROMETAL_INGOT_RED, RegistryReference.ITEM_KHROMETAL_INGOT_GREEN, RegistryReference.ITEM_KHROMETAL_INGOT_BLUE, RegistryReference.ITEM_KHROMETAL_INGOT_WHITE,
			RegistryReference.ITEM_KHROMETAL_INGOT_BLACK, RegistryReference.ITEM_CHROMATIC_NUCLEUS, RegistryReference.ITEM_CHROMATIC_GLASSES);

	public static final List<DeferredItem<Item>> handheldItems = List.of(RegistryReference.ITEM_KHROMETAL_RED_SWORD, RegistryReference.ITEM_KHROMETAL_RED_PICKAXE,
			RegistryReference.ITEM_KHROMETAL_GREEN_PICKAXE, RegistryReference.ITEM_KHROMETAL_BLUE_PICKAXE, RegistryReference.ITEM_KHROMETAL_WHITE_PICKAXE, RegistryReference.ITEM_KHROMETAL_BLACK_SWORD,
			RegistryReference.ITEM_KHROMETAL_BLACK_PICKAXE);

	public static final List<DeferredBlock<Block>> khromaDevices = List.of(RegistryReference.BLOCK_NODE_COLLECTOR, RegistryReference.BLOCK_KHROMA_PROVIDER,
			RegistryReference.BLOCK_KHROMA_MACHINE, RegistryReference.BLOCK_KHROMA_APERTURE,
			RegistryReference.BLOCK_KHROMA_COMBINER, RegistryReference.BLOCK_KHROMA_SEPARATOR, RegistryReference.BLOCK_KHROMA_IMBUER);

	public static final List<DeferredBlock<Block>> needsStoneTool = List.of(RegistryReference.BLOCK_CHROMIUM_ORE, RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE,
			RegistryReference.BLOCK_CHROMIUM_BLOCK, RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK);
}
