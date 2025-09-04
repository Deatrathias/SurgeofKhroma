package net.deatrathias.khroma.datagen;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import net.deatrathias.khroma.khroma.KhromaDeviceTier;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ItemReference;
import net.minecraft.data.BlockFamily;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DataGenDefinitions {

	public static record TreeDefinition(TagKey<Block> blockLogTag, TagKey<Item> itemLogTag, Block log, Block wood, Block strippedLog,
			Block strippedWood, Block leaves, Block sapling, Block pottedSapling, Block planks,
			BlockFamily family) {

	}

	// Simple cube-all models
	public static List<Block> cubeBlocks;

	// Blocks with no rotation and existing models
	public static List<Block> simpleBlocks;

	// Blocks with horizontal facing
	public static List<Block> horDirectionBlocks;

	// Blocks with full facing
	public static List<Block> fullDirectionBlocks;

	public static List<Block> dropsOreBlocks;

	public static List<Item> simpleItems;

	public static List<Item> handheldItems;

	public static List<Block> khromaDevices;

	public static List<Block> needsStoneTool;

	private static boolean initialized = false;

	public static <R, T extends R> T get(DeferredHolder<R, T> obj) {
		return obj.get();
	}

	@SafeVarargs
	private static <R, T extends R> List<T> deferredList(DeferredHolder<R, T>... elements) {
		return Arrays.stream(elements).map(DeferredHolder::get).toList();
	}

	@SafeVarargs
	private static <R, T extends R> List<T> deferredCombinedList(Collection<? extends DeferredHolder<R, T>>... elements) {
		Stream<DeferredHolder<R, T>> result = Stream.of();
		for (var element : elements) {
			result = Stream.concat(result, element.stream());
		}

		return result.map(DeferredHolder::get).toList();
	}

	public static void init() {
		if (initialized)
			return;
		initialized = true;

		cubeBlocks = deferredList(BlockReference.CHROMIUM_ORE, BlockReference.DEEPSLATE_CHROMIUM_ORE, BlockReference.CHROMIUM_BLOCK,
				BlockReference.RAW_CHROMIUM_BLOCK, BlockReference.KHROMETAL_BLOCK_RED, BlockReference.KHROMETAL_BLOCK_GREEN, BlockReference.KHROMETAL_BLOCK_BLUE,
				BlockReference.KHROMETAL_BLOCK_WHITE, BlockReference.KHROMETAL_BLOCK_BLACK);

		simpleBlocks = deferredCombinedList(List.of(BlockReference.NODE_COLLECTOR), sortedTierList(BlockReference.KHROMA_PROCESSING_CORE_TIERED));

		horDirectionBlocks = deferredList(BlockReference.KHROMA_PROVIDER, BlockReference.KHROMA_MACHINE,
				BlockReference.KHROMA_COMBINER, BlockReference.KHROMA_SEPARATOR, BlockReference.KHROMA_IMBUER);

		fullDirectionBlocks = deferredList(BlockReference.KHROMA_APERTURE, BlockReference.BREAK_BLOCK_PROCESS_CONTROLLER);

		dropsOreBlocks = deferredList(BlockReference.CHROMIUM_ORE, BlockReference.DEEPSLATE_CHROMIUM_ORE);

		simpleItems = deferredList(ItemReference.KHROMANCER_ARCHIVE, ItemReference.RAW_CHROMIUM, ItemReference.CHROMIUM_INGOT, ItemReference.CHROMIUM_NUGGET,
				ItemReference.KHROMETAL_INGOT_RED, ItemReference.KHROMETAL_INGOT_GREEN, ItemReference.KHROMETAL_INGOT_BLUE, ItemReference.KHROMETAL_INGOT_WHITE,
				ItemReference.KHROMETAL_INGOT_BLACK, ItemReference.CHROMATIC_NUCLEUS, ItemReference.CHROMATIC_GLASSES, ItemReference.STRIX_SPAWN_EGG);

		handheldItems = deferredList(ItemReference.KHROMETAL_RED_SWORD, ItemReference.KHROMETAL_RED_PICKAXE,
				ItemReference.KHROMETAL_GREEN_SWORD, ItemReference.KHROMETAL_GREEN_PICKAXE, ItemReference.KHROMETAL_BLUE_SWORD, ItemReference.KHROMETAL_BLUE_PICKAXE,
				ItemReference.KHROMETAL_WHITE_SWORD, ItemReference.KHROMETAL_WHITE_PICKAXE, ItemReference.KHROMETAL_BLACK_SWORD, ItemReference.KHROMETAL_BLACK_PICKAXE);

		khromaDevices = deferredCombinedList(List.of(BlockReference.KHROMA_LINE, BlockReference.NODE_COLLECTOR, BlockReference.KHROMA_PROVIDER,
				BlockReference.KHROMA_MACHINE, BlockReference.KHROMA_APERTURE, BlockReference.KHROMA_COMBINER, BlockReference.KHROMA_SEPARATOR,
				BlockReference.KHROMA_DISSIPATOR, BlockReference.KHROMA_IMBUER, BlockReference.ITEM_PEDESTAL), sortedTierList(BlockReference.KHROMA_PROCESSING_CORE_TIERED));

		needsStoneTool = deferredList(BlockReference.CHROMIUM_ORE, BlockReference.DEEPSLATE_CHROMIUM_ORE,
				BlockReference.CHROMIUM_BLOCK, BlockReference.RAW_CHROMIUM_BLOCK);
	}

	private static class TierEntryComparator implements Comparator<Map.Entry<KhromaDeviceTier, ?>> {

		@Override
		public int compare(Entry<KhromaDeviceTier, ?> o1, Entry<KhromaDeviceTier, ?> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}

	}

	private static <T> Collection<T> sortedTierList(Map<KhromaDeviceTier, T> tierMap) {
		return tierMap.entrySet().stream().sorted(new TierEntryComparator()).map(entry -> entry.getValue()).toList();
	}
}
