package net.deatrathias.khroma.datagen;

import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.TagReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import top.theillusivec4.curios.api.CuriosResources;

public final class TagsDataGen {

	public static class BlockTag extends BlockTagsProvider {

		public BlockTag(PackOutput output, CompletableFuture<Provider> lookupProviderr) {
			super(output, lookupProviderr, SurgeofKhroma.MODID);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void addTags(Provider provider) {
			var khrometalBlocks = tag(TagReference.BLOCK_KHROMETAL_BLOCKS);

			for (String khromaName : Khroma.KhromaNames)
				tagsPerKhroma(khrometalBlocks, khromaName);

			tag(TagReference.BLOCK_KHROMA_DEVICES).addAll(DataGenDefinitions.khromaDevices.stream().map(def -> def.getKey()).toList());

			tag(Tags.Blocks.ORES).addTag(TagReference.C_BLOCK_ORES_CHROMIUM);
			tag(TagReference.C_BLOCK_ORES_CHROMIUM).add(RegistryReference.BLOCK_CHROMIUM_ORE.getKey(), RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.getKey());
			tag(Tags.Blocks.ORE_RATES_SINGULAR).addTag(TagReference.C_BLOCK_ORES_CHROMIUM);
			tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(RegistryReference.BLOCK_CHROMIUM_ORE.getKey());
			tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.getKey());
			tag(TagReference.C_BLOCK_STORAGE_BLOCKS_CHROMIUM).add(RegistryReference.BLOCK_CHROMIUM_BLOCK.getKey());
			tag(TagReference.C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM).add(RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK.getKey());
			tag(Tags.Blocks.STORAGE_BLOCKS).addTags(TagReference.C_BLOCK_STORAGE_BLOCKS_CHROMIUM, TagReference.C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM, TagReference.BLOCK_KHROMETAL_BLOCKS);

			tag(BlockTags.NEEDS_STONE_TOOL).addAll(DataGenDefinitions.needsStoneTool.stream().map(def -> def.getKey()).toList()).addTag(TagReference.BLOCK_KHROMETAL_BLOCKS);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).addAll(DataGenDefinitions.needsStoneTool.stream().map(def -> def.getKey()).toList()).addTags(TagReference.BLOCK_KHROMETAL_BLOCKS,
					TagReference.BLOCK_KHROMA_DEVICES);
		}

		private void tagsPerKhroma(IntrinsicTagAppender<Block> khrometalBlocks, String khromaName) {
			khrometalBlocks.add(ResourceKey.create(Registries.BLOCK, SurgeofKhroma.resource("khrometal_block_" + khromaName)));
		}

	}

	public static class ItemTag extends ItemTagsProvider {

		public ItemTag(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
			super(output, lookupProvider, blockTags, SurgeofKhroma.MODID);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void addTags(Provider provider) {

			tag(TagReference.ITEM_BASE_INGOT).add(RegistryReference.ITEM_CHROMIUM_INGOT.getKey());
			tag(TagReference.ITEM_BASE_NUGGET).add(RegistryReference.ITEM_CHROMIUM_NUGGET.getKey());

			commonTags();
			minecraftTags();
			curiosTags();

			tag(TagReference.ITEM_CONVERTS_TO_DYE_RED_SINGULAR).add(Items.RED_TULIP, Items.BEETROOT, Items.POPPY);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_RED_DOUBLE).add(Items.ROSE_BUSH);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_RED_HALF);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_GREEN_SINGULAR).add(Items.CACTUS);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_GREEN_DOUBLE);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_GREEN_HALF).add(Items.KELP);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLUE_SINGULAR).add(Items.CORNFLOWER, Items.LAPIS_LAZULI);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLUE_DOUBLE);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLUE_HALF);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_WHITE_SINGULAR).add(Items.BONE_MEAL, Items.LILY_OF_THE_VALLEY);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_WHITE_DOUBLE);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_WHITE_HALF);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLACK_SINGULAR).add(Items.INK_SAC, Items.WITHER_ROSE);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLACK_DOUBLE);
			tag(TagReference.ITEM_CONVERTS_TO_DYE_BLACK_HALF);

			var swords = tag(TagReference.ITEM_KHROMETAL_SWORDS);
			var pickaxes = tag(TagReference.ITEM_KHROMETAL_PICKAXES);
			var axes = tag(TagReference.ITEM_KHROMETAL_AXES);
			var shovels = tag(TagReference.ITEM_KHROMETAL_SHOVELS);
			var khrometalIngots = tag(TagReference.ITEM_KHROMETAL_INGOTS);

			tag(TagReference.ITEM_KHROMETAL_TOOLS).addTags(TagReference.ITEM_KHROMETAL_SWORDS, TagReference.ITEM_KHROMETAL_PICKAXES, TagReference.ITEM_KHROMETAL_AXES,
					TagReference.ITEM_KHROMETAL_SHOVELS);

			for (String khromaName : Khroma.KhromaNames)
				tagsPerKhroma(swords, pickaxes, axes, shovels, khrometalIngots, khromaName);

			copyBlockTags();
		}

		@SuppressWarnings("unchecked")
		private void commonTags() {
			tag(TagReference.C_ITEM_INGOTS_CHROMIUM).add(RegistryReference.ITEM_CHROMIUM_INGOT.getKey());
			tag(Tags.Items.INGOTS).addTag(TagReference.C_ITEM_INGOTS_CHROMIUM);
			tag(TagReference.C_ITEM_NUGGETS_CHROMIUM).add(RegistryReference.ITEM_CHROMIUM_NUGGET.getKey());
			tag(Tags.Items.NUGGETS).addTag(TagReference.C_ITEM_NUGGETS_CHROMIUM);
			tag(TagReference.C_ITEM_RAW_MATERIALS_CHROMIUM).add(RegistryReference.ITEM_RAW_CHROMIUM.getKey());
			tag(Tags.Items.RAW_MATERIALS).addTag(TagReference.C_ITEM_RAW_MATERIALS_CHROMIUM);
			tag(Tags.Items.MELEE_WEAPON_TOOLS).addTags(TagReference.ITEM_KHROMETAL_SWORDS, TagReference.ITEM_KHROMETAL_AXES);
			tag(Tags.Items.MINING_TOOL_TOOLS).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
		}

		private void minecraftTags() {
			tag(ItemTags.PICKAXES).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
			tag(ItemTags.CLUSTER_MAX_HARVESTABLES).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
			tag(ItemTags.AXES).addTag(TagReference.ITEM_KHROMETAL_AXES);
			tag(ItemTags.SHOVELS).addTag(TagReference.ITEM_KHROMETAL_SHOVELS);
			tag(ItemTags.SWORDS).addTag(TagReference.ITEM_KHROMETAL_SWORDS);
		}

		private void curiosTags() {
			tag(ItemTags.create(CuriosResources.resource("eyes"))).add(RegistryReference.ITEM_CHROMATIC_GLASSES.getKey());
		}

		private void tagsPerKhroma(IntrinsicTagAppender<Item> swords, IntrinsicTagAppender<Item> pickaxes, IntrinsicTagAppender<Item> axes, IntrinsicTagAppender<Item> shovel,
				IntrinsicTagAppender<Item> khrometalIngots, String khromaName) {
			var itemRegistry = BuiltInRegistries.ITEM;
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_sword")).ifPresent(item -> swords.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_pickaxe")).ifPresent(item -> pickaxes.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_axe")).ifPresent(item -> axes.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_shovel")).ifPresent(item -> shovel.add(item));
			var ingot = ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_ingot_" + khromaName));

			var ingotTag = ItemTags.create(SurgeofKhroma.resource("ingots/khrometal/" + khromaName));
			tag(ingotTag).add(ingot);
			khrometalIngots.addTag(ingotTag);
		}

		private void copyBlockTags() {
			copy(TagReference.BLOCK_KHROMETAL_BLOCKS, TagReference.ITEM_KHROMETAL_BLOCKS);
			copy(TagReference.BLOCK_KHROMA_DEVICES, TagReference.ITEM_KHROMA_DEVICES);
			copy(Tags.Blocks.ORES, Tags.Items.ORES);
			copy(TagReference.C_BLOCK_ORES_CHROMIUM, TagReference.C_ITEM_ORES_CHROMIUM);
			copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
			copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
			copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
			copy(TagReference.C_BLOCK_STORAGE_BLOCKS_CHROMIUM, TagReference.C_ITEM_STORAGE_BLOCKS_CHROMIUM);
			copy(TagReference.C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM, TagReference.C_ITEM_STORAGE_BLOCKS_RAW_CHROMIUM);
			copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
		}
	}

	public static class DamageTypeTag extends DamageTypeTagsProvider {

		public DamageTypeTag(PackOutput output, CompletableFuture<Provider> lookupProvider) {
			super(output, lookupProvider, SurgeofKhroma.MODID);
		}

		@Override
		protected void addTags(Provider provider) {
			tag(DamageTypeTags.NO_KNOCKBACK).addOptional(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK.location());
		}
	}
}
