package net.deatrathias.khroma;

import java.util.function.Supplier;

import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaCombinerBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaMachineBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaProviderBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaSeparatorBlockEntity;
import net.deatrathias.khroma.blocks.KhromaApertureBlock;
import net.deatrathias.khroma.blocks.KhromaCombinerBlock;
import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.blocks.KhromaMachineBlock;
import net.deatrathias.khroma.blocks.KhromaProviderBlock;
import net.deatrathias.khroma.blocks.KhromaSeparatorBlock;
import net.deatrathias.khroma.blocks.items.KromaLineBlockItem;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistryReference {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SurgeofKhroma.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SurgeofKhroma.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SurgeofKhroma.MODID);

	/**
	 * 
	 * BLOCKS
	 * 
	 */
	public static final DeferredBlock<Block> BLOCK_CHROMIUM_ORE = BLOCKS.register("chromium_ore", registryName -> new DropExperienceBlock(ConstantInt.of(0),
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_CHROMIUM_ORE = ITEMS.registerSimpleBlockItem(BLOCK_CHROMIUM_ORE);

	public static final DeferredBlock<Block> BLOCK_DEEPSLATE_CHROMIUM_ORE = BLOCKS.register("deepslate_chromium_ore", registryName -> new DropExperienceBlock(ConstantInt.of(0),
			BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE = ITEMS.registerSimpleBlockItem(BLOCK_DEEPSLATE_CHROMIUM_ORE);

	public static final DeferredBlock<Block> BLOCK_CHROMIUM_BLOCK = BLOCKS.register("chromium_block", registryName -> new Block(
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_CHROMIUM_BLOCK = ITEMS.registerSimpleBlockItem(BLOCK_CHROMIUM_BLOCK);

	public static final DeferredBlock<Block> BLOCK_RAW_CHROMIUM_BLOCK = BLOCKS.register("raw_chromium_block",
			registryName -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_RAW_CHROMIUM_BLOCK = ITEMS.registerSimpleBlockItem(BLOCK_RAW_CHROMIUM_BLOCK);

	public static final DeferredBlock<Block> BLOCK_KHROMA_LINE = BLOCKS.register("khroma_line",
			registryName -> new KhromaLineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.1F, 1.0F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
	public static final DeferredItem<Item> ITEM_BLOCK_KHROMA_LINE = ITEMS.register("khroma_line", registryName -> new KromaLineBlockItem(BLOCK_KHROMA_LINE.get(), new Item.Properties()));

	public static final DeferredBlock<Block> BLOCK_KHROMA_PROVIDER = BLOCKS.register("khroma_provider", registryName -> new KhromaProviderBlock(BlockBehaviour.Properties.of()));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_PROVIDER = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_PROVIDER);

	public static final DeferredBlock<Block> BLOCK_KHROMA_MACHINE = BLOCKS.register("khroma_machine", registryName -> new KhromaMachineBlock(BlockBehaviour.Properties.of()));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_MACHINE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_MACHINE);

	public static final DeferredBlock<Block> BLOCK_KHROMA_APERTURE = BLOCKS.register("khroma_aperture",
			registryName -> new KhromaApertureBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_APERTURE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_APERTURE);

	public static final DeferredBlock<Block> BLOCK_KHROMA_COMBINER = BLOCKS.register("khroma_combiner",
			registryName -> new KhromaCombinerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_COMBINER = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_COMBINER);

	public static final DeferredBlock<Block> BLOCK_KHROMA_SEPARATOR = BLOCKS.register("khroma_separator",
			registryName -> new KhromaSeparatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_SEPARATOR = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_SEPARATOR);

	/**
	 * 
	 * BLOCK ENTITY
	 * 
	 */
	public static final Supplier<BlockEntityType<KhromaProviderBlockEntity>> BLOCK_ENTITY_KHROMA_PROVIDER = BLOCK_ENTITY_TYPES.register("khroma_provider_entity",
			() -> (BlockEntityType.Builder.of(KhromaProviderBlockEntity::new, BLOCK_KHROMA_PROVIDER.get()).build(null)));

	public static final Supplier<BlockEntityType<KhromaMachineBlockEntity>> BLOCK_ENTITY_KHROMA_MACHINE = BLOCK_ENTITY_TYPES.register("khroma_machine_entity",
			() -> (BlockEntityType.Builder.of(KhromaMachineBlockEntity::new, BLOCK_KHROMA_MACHINE.get()).build(null)));

	public static final Supplier<BlockEntityType<KhromaApertureBlockEntity>> BLOCK_ENTITY_KHROMA_APERTURE = BLOCK_ENTITY_TYPES.register("khroma_aperture_entity",
			() -> (BlockEntityType.Builder.of(KhromaApertureBlockEntity::new, BLOCK_KHROMA_APERTURE.get()).build(null)));

	public static final Supplier<BlockEntityType<KhromaCombinerBlockEntity>> BLOCK_ENTITY_KHROMA_COMBINER = BLOCK_ENTITY_TYPES.register("khroma_combiner_entity",
			() -> (BlockEntityType.Builder.of(KhromaCombinerBlockEntity::new, BLOCK_KHROMA_COMBINER.get()).build(null)));

	public static final Supplier<BlockEntityType<KhromaSeparatorBlockEntity>> BLOCK_ENTITY_KHROMA_SEPARATOR = BLOCK_ENTITY_TYPES.register("khroma_separator_entity",
			() -> (BlockEntityType.Builder.of(KhromaSeparatorBlockEntity::new, BLOCK_KHROMA_SEPARATOR.get()).build(null)));

	/**
	 * 
	 * ITEMS
	 * 
	 */
	public static final DeferredItem<Item> ITEM_RAW_CHROMIUM = ITEMS.registerSimpleItem("raw_chromium");
	public static final DeferredItem<Item> ITEM_CHROMIUM_INGOT = ITEMS.registerSimpleItem("chromium_ingot");
	public static final DeferredItem<Item> ITEM_CHROMIUM_NUGGET = ITEMS.registerSimpleItem("chromium_nugget");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_RED = ITEMS.registerSimpleItem("khrometal_ingot_red");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_GREEN = ITEMS.registerSimpleItem("khrometal_ingot_green");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_BLUE = ITEMS.registerSimpleItem("khrometal_ingot_blue");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_WHITE = ITEMS.registerSimpleItem("khrometal_ingot_white");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_BLACK = ITEMS.registerSimpleItem("khrometal_ingot_black");
	public static final DeferredItem<Item> ITEM_CHROMATIC_NUCLEUS = ITEMS.registerSimpleItem("chromatic_nucleus");

	/**
	 * 
	 * MENUS
	 * 
	 */
	public static final Supplier<MenuType<KhromaApertureMenu>> MENU_KHROMA_APERTURE = MENUS.register("khroma_aperture_menu",
			() -> new MenuType<KhromaApertureMenu>(KhromaApertureMenu::new, FeatureFlags.DEFAULT_FLAGS));

	public static final BlockCapability<IKhromaProvider, Direction> KHROMA_PROVIDER_BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "khroma_provider"),
			IKhromaProvider.class);
	public static final BlockCapability<IKhromaConsumer, Direction> KHROMA_CONSUMER_BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "khroma_consumer"),
			IKhromaConsumer.class);

	// Creates a creative tab with the id "surgeofkhroma:example_tab" for the
	// example item, that is placed after the combat tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = SurgeofKhroma.CREATIVE_MODE_TABS.register("example_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("itemGroup.surgeofkhroma"))
					// The language key for the title of your CreativeModeTab
					.withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ITEM_CHROMATIC_NUCLEUS.get().getDefaultInstance()).displayItems((parameters, output) -> {
						output.accept(ITEM_CHROMIUM_INGOT.get());
						output.accept(ITEM_BLOCK_CHROMIUM_ORE.get());
						output.accept(ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE.get());
						output.accept(ITEM_BLOCK_CHROMIUM_BLOCK.get());
						output.accept(ITEM_BLOCK_RAW_CHROMIUM_BLOCK.get());
						output.accept(ITEM_RAW_CHROMIUM.get());
						output.accept(ITEM_CHROMIUM_NUGGET.get());
						output.accept(ITEM_KHROMETAL_INGOT_RED.get());
						output.accept(ITEM_KHROMETAL_INGOT_GREEN.get());
						output.accept(ITEM_KHROMETAL_INGOT_BLUE.get());
						output.accept(ITEM_KHROMETAL_INGOT_WHITE.get());
						output.accept(ITEM_KHROMETAL_INGOT_BLACK.get());
						output.accept(ITEM_CHROMATIC_NUCLEUS.get());
						output.accept(ITEM_BLOCK_KHROMA_LINE.get());
						output.accept(ITEM_BLOCK_KHROMA_APERTURE.get());
						output.accept(ITEM_BLOCK_KHROMA_COMBINER.get());
						output.accept(ITEM_BLOCK_KHROMA_SEPARATOR.get());
					}).build());
}
