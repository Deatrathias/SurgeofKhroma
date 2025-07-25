package net.deatrathias.khroma.datagen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.TagReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import top.theillusivec4.curios.api.CuriosResources;

public final class TagsDataGen {

	public abstract static class BlockItemTag extends BlockItemTagsProvider {
		@SuppressWarnings("unchecked")
		@Override
		protected void run() {
			tag(TagReference.BLOCK_KHROMETAL_BLOCKS, TagReference.ITEM_KHROMETAL_BLOCKS).add(
					RegistryReference.BLOCK_KHROMETAL_BLOCK_RED.get(),
					RegistryReference.BLOCK_KHROMETAL_BLOCK_GREEN.get(),
					RegistryReference.BLOCK_KHROMETAL_BLOCK_BLUE.get(),
					RegistryReference.BLOCK_KHROMETAL_BLOCK_WHITE.get(),
					RegistryReference.BLOCK_KHROMETAL_BLOCK_BLACK.get());

			tag(TagReference.BLOCK_KHROMA_DEVICES, TagReference.ITEM_KHROMA_DEVICES).addAll(DataGenDefinitions.khromaDevices.stream().map(def -> def.get()).toList());
			tag(Tags.Blocks.ORES, Tags.Items.ORES).addTag(TagReference.C_BLOCK_ORES_CHROMIUM);
			tag(TagReference.C_BLOCK_ORES_CHROMIUM, TagReference.C_ITEM_ORES_CHROMIUM).add(RegistryReference.BLOCK_CHROMIUM_ORE.get(), RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.get());
			tag(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR).addTag(TagReference.C_BLOCK_ORES_CHROMIUM);
			tag(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE).add(RegistryReference.BLOCK_CHROMIUM_ORE.get());
			tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.get());

			tag(TagReference.C_BLOCK_STORAGE_BLOCKS_CHROMIUM, TagReference.C_ITEM_STORAGE_BLOCKS_CHROMIUM).add(RegistryReference.BLOCK_CHROMIUM_BLOCK.get());
			tag(TagReference.C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM, TagReference.C_ITEM_STORAGE_BLOCKS_RAW_CHROMIUM).add(RegistryReference.BLOCK_RAW_CHROMIUM_BLOCK.get());
			tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS).addTags(TagReference.C_BLOCK_STORAGE_BLOCKS_CHROMIUM, TagReference.C_BLOCK_STORAGE_BLOCKS_RAW_CHROMIUM,
					TagReference.BLOCK_KHROMETAL_BLOCKS);
		}

	}

	public static class BlockTag extends BlockTagsProvider {

		public BlockTag(PackOutput output, CompletableFuture<Provider> lookupProviderr) {
			super(output, lookupProviderr, SurgeofKhroma.MODID);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void addTags(Provider provider) {
			(new TagsDataGen.BlockItemTag() {
				@Override
				protected TagAppender<Block, Block> tag(TagKey<Block> blockTag, TagKey<Item> itemTag) {
					return BlockTag.this.tag(blockTag);
				}
			}).run();

			tag(BlockTags.NEEDS_STONE_TOOL).addAll(DataGenDefinitions.needsStoneTool.stream().map(def -> def.get()).toList()).addTag(TagReference.BLOCK_KHROMETAL_BLOCKS);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).addAll(DataGenDefinitions.needsStoneTool.stream().map(def -> def.get()).toList()).addTags(TagReference.BLOCK_KHROMETAL_BLOCKS,
					TagReference.BLOCK_KHROMA_DEVICES);
		}
	}

	public static class ItemTag extends ItemTagsProvider {

		public ItemTag(PackOutput output, CompletableFuture<Provider> lookupProvider) {
			super(output, lookupProvider, SurgeofKhroma.MODID);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void addTags(Provider provider) {
			(new TagsDataGen.BlockItemTag() {
				@Override
				protected TagAppender<Block, Block> tag(TagKey<Block> blockTag, TagKey<Item> itemTag) {
					return new BlockToItemConverter(ItemTag.this.tag(itemTag));
				}
			}).run();

			tag(TagReference.ITEM_BASE_INGOT).add(RegistryReference.ITEM_CHROMIUM_INGOT.get());
			tag(TagReference.ITEM_BASE_NUGGET).add(RegistryReference.ITEM_CHROMIUM_NUGGET.get());

			commonTags();
			minecraftTags();
			if (ModList.get().isLoaded(CuriosResources.MOD_ID))
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
		}

		@SuppressWarnings("unchecked")
		private void commonTags() {
			tag(TagReference.C_ITEM_INGOTS_CHROMIUM).add(RegistryReference.ITEM_CHROMIUM_INGOT.get());
			tag(Tags.Items.INGOTS).addTags(TagReference.C_ITEM_INGOTS_CHROMIUM, TagReference.ITEM_KHROMETAL_INGOTS);
			tag(TagReference.C_ITEM_NUGGETS_CHROMIUM).add(RegistryReference.ITEM_CHROMIUM_NUGGET.get());
			tag(Tags.Items.NUGGETS).addTag(TagReference.C_ITEM_NUGGETS_CHROMIUM);
			tag(TagReference.C_ITEM_RAW_MATERIALS_CHROMIUM).add(RegistryReference.ITEM_RAW_CHROMIUM.get());
			tag(Tags.Items.RAW_MATERIALS).addTag(TagReference.C_ITEM_RAW_MATERIALS_CHROMIUM);
			tag(Tags.Items.MELEE_WEAPON_TOOLS).addTags(TagReference.ITEM_KHROMETAL_SWORDS, TagReference.ITEM_KHROMETAL_AXES);
			tag(Tags.Items.MINING_TOOL_TOOLS).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
			tag(Tags.Items.TOOLS_WRENCH).add(RegistryReference.ITEM_KHROMETAL_SPANNER.get());
		}

		private void minecraftTags() {
			tag(ItemTags.PICKAXES).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
			tag(ItemTags.CLUSTER_MAX_HARVESTABLES).addTag(TagReference.ITEM_KHROMETAL_PICKAXES);
			tag(ItemTags.AXES).addTag(TagReference.ITEM_KHROMETAL_AXES);
			tag(ItemTags.SHOVELS).addTag(TagReference.ITEM_KHROMETAL_SHOVELS);
			tag(ItemTags.SWORDS).addTag(TagReference.ITEM_KHROMETAL_SWORDS);
		}

		private void curiosTags() {
			tag(ItemTags.create(CuriosResources.resource("eyes"))).add(RegistryReference.ITEM_CHROMATIC_GLASSES.get());
		}

		private void tagsPerKhroma(TagAppender<Item, Item> swords, TagAppender<Item, Item> pickaxes, TagAppender<Item, Item> axes, TagAppender<Item, Item> shovel,
				TagAppender<Item, Item> khrometalIngots, String khromaName) {
			var itemRegistry = BuiltInRegistries.ITEM;
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_sword")).ifPresent(item -> swords.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_pickaxe")).ifPresent(item -> pickaxes.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_axe")).ifPresent(item -> axes.add(item));
			itemRegistry.getOptional(SurgeofKhroma.resource("khrometal_" + khromaName + "_shovel")).ifPresent(item -> shovel.add(item));
			var ingot = ResourceKey.create(Registries.ITEM, SurgeofKhroma.resource("khrometal_ingot_" + khromaName));

			var ingotTag = ItemTags.create(SurgeofKhroma.resource("ingots/khrometal/" + khromaName));
			tag(ingotTag).add(itemRegistry.getValueOrThrow(ingot));
			khrometalIngots.addTag(ingotTag);
		}

		static class BlockToItemConverter implements TagAppender<Block, Block> {
			private final TagAppender<Item, Item> itemAppender;

			public BlockToItemConverter(TagAppender<Item, Item> itemAppender) {
				this.itemAppender = itemAppender;
			}

			public TagAppender<Block, Block> add(Block p_422488_) {
				this.itemAppender.add(Objects.requireNonNull(p_422488_.asItem()));
				return this;
			}

			public TagAppender<Block, Block> addOptional(Block p_422522_) {
				this.itemAppender.addOptional(Objects.requireNonNull(p_422522_.asItem()));
				return this;
			}

			private static TagKey<Item> blockTagToItemTag(TagKey<Block> tag) {
				return TagKey.create(Registries.ITEM, tag.location());
			}

			@Override
			public TagAppender<Block, Block> addTag(TagKey<Block> p_422282_) {
				this.itemAppender.addTag(blockTagToItemTag(p_422282_));
				return this;
			}

			@Override
			public TagAppender<Block, Block> addOptionalTag(TagKey<Block> p_422609_) {
				this.itemAppender.addOptionalTag(blockTagToItemTag(p_422609_));
				return this;
			}

			@Override
			public TagAppender<Block, Block> add(net.minecraft.tags.TagEntry entry) {
				itemAppender.add(entry);
				return this;
			}

			@Override
			public TagAppender<Block, Block> replace(boolean value) {
				itemAppender.replace(value);
				return this;
			}

			@Override
			public TagAppender<Block, Block> remove(Block block) {
				itemAppender.remove(block.asItem());
				return this;
			}

			@Override
			public TagAppender<Block, Block> remove(TagKey<Block> tag) {
				itemAppender.remove(blockTagToItemTag(tag));
				return this;
			}
		}
	}

	public static class DamageTypeTag extends DamageTypeTagsProvider {

		public DamageTypeTag(PackOutput output, CompletableFuture<Provider> lookupProvider) {
			super(output, lookupProvider, SurgeofKhroma.MODID);
		}

		@Override
		protected void addTags(Provider provider) {
			tag(DamageTypeTags.NO_KNOCKBACK).add(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK);
		}
	}

	public static class EntityTypeTag extends EntityTypeTagsProvider {

		public EntityTypeTag(PackOutput output, CompletableFuture<Provider> provider) {
			super(output, provider, SurgeofKhroma.MODID);
		}

		@Override
		protected void addTags(Provider provider) {
			tag(TagReference.C_ENTITY_BIRDS).add(EntityType.CHICKEN, EntityType.PARROT);
			tag(TagReference.ENTITY_SENSITIVE_TO_FEATHERCLIP).addTag(TagReference.C_ENTITY_BIRDS);
		}
	}

	public static class EnchantmentTag extends EnchantmentTagsProvider {

		public EnchantmentTag(PackOutput output, CompletableFuture<Provider> lookupProvider) {
			super(output, lookupProvider, SurgeofKhroma.MODID);
		}

		@Override
		protected void addTags(Provider provider) {
			var featherclipKey = SurgeofKhroma.resourceKey(Registries.ENCHANTMENT, "featherclip");
			tag(EnchantmentTags.DAMAGE_EXCLUSIVE).add(featherclipKey);
			tag(EnchantmentTags.NON_TREASURE).add(featherclipKey);
			tag(EnchantmentTags.TOOLTIP_ORDER).add(featherclipKey);
		}

	}
}
