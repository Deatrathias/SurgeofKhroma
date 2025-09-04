package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.EntityReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.ItemReference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class LootTableDataGen extends LootTableProvider {

	public LootTableDataGen(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, Set.of(), List.of(new SubProviderEntry(BlockLoots::new, LootContextParamSets.BLOCK), new SubProviderEntry(EntityLoots::new, LootContextParamSets.ENTITY)), registries);
	}

	private static class BlockLoots extends BlockLootSubProvider {

		protected BlockLoots(HolderLookup.Provider lookupProvider) {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return (Iterable<Block>) BlockReference.BLOCKS.getEntries().stream().map(def -> def.get()).toList();
		}

		@Override
		protected void generate() {
			add(BlockReference.CHROMIUM_ORE.get(), block -> createOreDrop(block, ItemReference.RAW_CHROMIUM.get()));
			add(BlockReference.DEEPSLATE_CHROMIUM_ORE.get(), block -> createOreDrop(block, ItemReference.RAW_CHROMIUM.get()));
			for (var tree : BlockReference.IMBUED_TREES) {
				add(tree.get(TreeBlock.LEAVES), createLeavesDrops(tree.get(TreeBlock.LEAVES), tree.get(TreeBlock.SAPLING), NORMAL_LEAVES_SAPLING_CHANCES));
				dropPottedContents(tree.get(TreeBlock.POTTED_SAPLING));
				tree.ifPresent(TreeBlock.DOOR, block -> add(block, createDoorTable(block)));
			}

			for (var element : BlockReference.BLOCKS.getEntries()) {
				Block block = element.get();
				if (!map.containsKey(block.getLootTable().get()) && block.asItem() != Items.AIR)
					dropSelf(block);
			}
		}

	}

	private static class EntityLoots extends EntityLootSubProvider {
		protected EntityLoots(Provider registries) {
			super(FeatureFlags.DEFAULT_FLAGS, registries);
		}

		@Override
		protected Stream<EntityType<?>> getKnownEntityTypes() {
			return Stream.of(EntityReference.STRIX.get());
		}

		@Override
		public void generate() {
			add(EntityReference.STRIX.get(), LootTable.lootTable()
					.withPool(
							LootPool.lootPool()
									.setRolls(ConstantValue.exactly(1f))
									.add(LootItem.lootTableItem(Items.FEATHER))
									.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
									.apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1)))));
		}

	}
}
