package net.deatrathias.khroma.registries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaImbuerBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaProcessingCoreBlockEntity;
import net.deatrathias.khroma.blocks.items.KromaLineBlockItem;
import net.deatrathias.khroma.blocks.items.NodeCollectorBlockItem;
import net.deatrathias.khroma.blocks.khrometal.KhrometalBlackBlock;
import net.deatrathias.khroma.blocks.khrometal.KhrometalBlueBlock;
import net.deatrathias.khroma.blocks.khrometal.KhrometalGreenBlock;
import net.deatrathias.khroma.blocks.khrometal.KhrometalRedBlock;
import net.deatrathias.khroma.blocks.khrometal.KhrometalWhiteBlock;
import net.deatrathias.khroma.blocks.logistics.KhromaApertureBlock;
import net.deatrathias.khroma.blocks.logistics.KhromaCombinerBlock;
import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.blocks.logistics.KhromaSeparatorBlock;
import net.deatrathias.khroma.blocks.machine.KhromaDissipatorBlock;
import net.deatrathias.khroma.blocks.machine.KhromaImbuerBlock;
import net.deatrathias.khroma.blocks.machine.KhromaMachineBlock;
import net.deatrathias.khroma.blocks.machine.KhromaProviderBlock;
import net.deatrathias.khroma.blocks.machine.NodeCollectorBlock;
import net.deatrathias.khroma.blocks.machine.modular.KhromaProcessingCoreBlock;
import net.deatrathias.khroma.khroma.KhromaDeviceTier;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockReference {

	public static class BlockSetReference {
		public static final BlockSetType SPARKTREE = BlockSetType.register(new BlockSetType(SurgeofKhroma.MODID + ":sparktree"));
		public static final BlockSetType BLOOMTREE = BlockSetType.register(new BlockSetType(SurgeofKhroma.MODID + ":bloomtree"));
		public static final BlockSetType FLOWTREE = BlockSetType.register(new BlockSetType(SurgeofKhroma.MODID + ":flowtree"));
		public static final BlockSetType SKYTREE = BlockSetType.register(new BlockSetType(SurgeofKhroma.MODID + ":skytree"));
		public static final BlockSetType GRIMTREE = BlockSetType.register(new BlockSetType(SurgeofKhroma.MODID + ":grimtree"));
	}

	public static class WoodReference {
		public static final WoodType SPARKTREE = WoodType.register(new WoodType(SurgeofKhroma.MODID + ":sparktree", BlockSetReference.SPARKTREE));
		public static final WoodType BLOOMTREE = WoodType.register(new WoodType(SurgeofKhroma.MODID + ":bloomtree", BlockSetReference.BLOOMTREE));
		public static final WoodType FLOWTREE = WoodType.register(new WoodType(SurgeofKhroma.MODID + ":flowtree", BlockSetReference.FLOWTREE));
		public static final WoodType SKYTREE = WoodType.register(new WoodType(SurgeofKhroma.MODID + ":skytree", BlockSetReference.SKYTREE));
		public static final WoodType GRIMTREE = WoodType.register(new WoodType(SurgeofKhroma.MODID + ":grimtree", BlockSetReference.GRIMTREE));
	}

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SurgeofKhroma.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SurgeofKhroma.MODID);

	public static final DeferredBlock<Block> CHROMIUM_ORE = registerBlock("chromium_ore", properties -> new DropExperienceBlock(ConstantInt.ZERO, properties), blockProps()
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(3.0F, 3.0F));
	public static final DeferredBlock<Block> DEEPSLATE_CHROMIUM_ORE = registerBlock("deepslate_chromium_ore", properties -> new DropExperienceBlock(ConstantInt.ZERO, properties), blockProps()
			.mapColor(MapColor.DEEPSLATE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(4.5F, 3.0F)
			.sound(SoundType.DEEPSLATE));

	public static final DeferredBlock<Block> CHROMIUM_BLOCK = registerBlock("chromium_block", Block::new, metalBlockProps(MapColor.METAL));

	public static final DeferredBlock<Block> RAW_CHROMIUM_BLOCK = registerBlock("raw_chromium_block", Block::new, blockProps()
			.mapColor(MapColor.RAW_IRON)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(5.0F, 6.0F));

	public static final DeferredBlock<Block> KHROMETAL_BLOCK_RED = registerBlock("khrometal_block_red", KhrometalRedBlock::new, metalBlockProps(MapColor.COLOR_RED));
	public static final DeferredBlock<Block> KHROMETAL_BLOCK_GREEN = registerBlock("khrometal_block_green", KhrometalGreenBlock::new, metalBlockProps(MapColor.COLOR_GREEN));
	public static final DeferredBlock<Block> KHROMETAL_BLOCK_BLUE = registerBlock("khrometal_block_blue", KhrometalBlueBlock::new, metalBlockProps(MapColor.COLOR_BLUE));
	public static final DeferredBlock<Block> KHROMETAL_BLOCK_WHITE = registerBlock("khrometal_block_white", KhrometalWhiteBlock::new, metalBlockProps(MapColor.QUARTZ));
	public static final DeferredBlock<Block> KHROMETAL_BLOCK_BLACK = registerBlock("khrometal_block_black", KhrometalBlackBlock::new, metalBlockProps(MapColor.COLOR_BLACK));

//	public static final DeferredBlock<Block> STRIPPED_SPARKTREE_LOG = registerBlock("stripped_sparktree_log", properties -> new ImbuedLogBlock(properties, false, null),
//			logProperties(MapColor.COLOR_RED, MapColor.COLOR_RED));
//	public static final DeferredBlock<Block> STRIPPED_SPARKTREE_WOOD = registerBlock("stripped_sparktree_wood", properties -> new ImbuedLogBlock(properties, false, null),
//			woodProperties(MapColor.COLOR_RED));
//	public static final DeferredBlock<Block> SPARKTREE_LOG = registerBlock("sparktree_log", properties -> new ImbuedLogBlock(properties, false, STRIPPED_SPARKTREE_LOG),
//			logProperties(MapColor.TERRACOTTA_BROWN, MapColor.COLOR_RED));
//	public static final DeferredBlock<Block> SPARKTREE_WOOD = registerBlock("sparktree_wood", properties -> new ImbuedLogBlock(properties, false, STRIPPED_SPARKTREE_WOOD),
//			woodProperties(MapColor.TERRACOTTA_BROWN));
//	public static final DeferredBlock<Block> SPARKTREE_LEAVES = registerBlock("sparktree_leaves", properties -> new FixedTintParticleLeavesBlock(0.01F, 0xffe42121, properties),
//			leavesProperties(SoundType.GRASS));
//	public static final DeferredBlock<Block> SPARKTREE_SAPLING = registerBlock("sparktree_sapling", properties -> new SaplingBlock(TreeGrowerReference.SPARKTREE, properties), saplingProperties());
//	@SuppressWarnings("deprecation")
//	public static final DeferredBlock<Block> POTTED_SPARKTREE_SAPLING = registerBlockNoItem("potted_sparktree_sapling",
//			properties -> new FlowerPotBlock(SPARKTREE_SAPLING.get(), properties), flowerPotProperties());
//	public static final DeferredBlock<Block> SPARKTREE_PLANKS = registerBlock("sparktree_planks", Block::new, plankProperties(MapColor.COLOR_RED));
//
//	public static final DeferredBlock<Block> SPARKTREE_BUTTON = registerBlock("sparktree_button", properties -> new ButtonBlock(BlockSetReference.SPARKTREE, 30, properties), buttonProperties());
//	public static final DeferredBlock<Block> SPARKTREE_FENCE = registerBlock("sparktree_fence", FenceBlock::new, plankProperties(MapColor.COLOR_RED).forceSolidOn());
//	public static final DeferredBlock<Block> SPARKTREE_FENCE_GATE = registerBlock("sparktree_fence_gate", properties -> new FenceGateBlock(WoodReference.SPARKTREE, properties),
//			plankProperties(MapColor.COLOR_RED).forceSolidOn());

	public static final ImbuedTree SPARKTREE = ImbuedTree.Builder.create("sparktree")
			.log(MapColor.TERRACOTTA_BROWN, MapColor.COLOR_RED)
			.wood(MapColor.TERRACOTTA_BROWN)
			.strippedLog(MapColor.COLOR_RED, MapColor.COLOR_RED)
			.strippedWood(MapColor.COLOR_RED)
			.leaves(SoundType.GRASS, 0.01f, 0xffe42121)
			.sapling(TreeGrowerReference.SPARKTREE)
			.pottedSapling()
			.planks(MapColor.COLOR_RED)
			.button()
			.fence()
			.fenceGate()
			.slab()
			.stairs()
			.pressurePlate()
			.door()
			.trapdoor()
			.sign()
			.hangingSign()
			.boat()
			.chestBoat()
			.build(BlockSetReference.SPARKTREE, WoodReference.SPARKTREE);

	public static final ImbuedTree BLOOMTREE = ImbuedTree.Builder.create("bloomtree")
			.log(p -> p.strength(2f, 6f), MapColor.TERRACOTTA_GREEN, MapColor.COLOR_GREEN)
			.wood(p -> p.strength(2f, 6f), MapColor.TERRACOTTA_GREEN)
			.strippedLog(p -> p.strength(2f, 6f), MapColor.COLOR_GREEN, MapColor.COLOR_GREEN)
			.strippedWood(p -> p.strength(2f, 6f), MapColor.COLOR_GREEN)
			.leaves(SoundType.GRASS, 0.01f, 0xff21e421)
			.sapling(TreeGrowerReference.BLOOMTREE)
			.pottedSapling()
			.planks(p -> p.strength(2f, 6f), MapColor.COLOR_GREEN)
			.button()
			.fence(p -> p.strength(2f, 6f))
			.fenceGate(p -> p.strength(2f, 6f))
			.slab(p -> p.strength(2f, 6f))
			.stairs(p -> p.strength(2f, 6f))
			.pressurePlate()
			.door(p -> p.strength(3f, 6f))
			.trapdoor(p -> p.strength(3f, 6f))
			.sign()
			.hangingSign()
			.boat()
			.chestBoat()
			.build(BlockSetReference.BLOOMTREE, WoodReference.BLOOMTREE);

	public static final List<ImbuedTree> IMBUED_TREES = List.of(SPARKTREE, BLOOMTREE);

	public static final DeferredBlock<Block> KHROMA_LINE = registerBlock("khroma_line", KhromaLineBlock::new,
			blockProps().mapColor(MapColor.METAL).strength(0.1F, 1.0F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY), KromaLineBlockItem::new, new Item.Properties());

	public static final DeferredBlock<Block> NODE_COLLECTOR = registerBlock("node_collector", NodeCollectorBlock::new, khromaDeviceProps().strength(10f, 6f).noOcclusion(),
			NodeCollectorBlockItem::new, new Item.Properties());

	public static final DeferredBlock<Block> KHROMA_PROVIDER = registerBlock("khroma_provider", KhromaProviderBlock::new, khromaDeviceProps());

	public static final DeferredBlock<Block> KHROMA_MACHINE = registerBlock("khroma_machine", KhromaMachineBlock::new, khromaDeviceProps());

	public static final DeferredBlock<Block> KHROMA_APERTURE = registerBlock("khroma_aperture", KhromaApertureBlock::new, khromaDeviceProps(true));
	public static final DeferredBlock<Block> KHROMA_COMBINER = registerBlock("khroma_combiner", KhromaCombinerBlock::new, khromaDeviceProps(true));
	public static final DeferredBlock<Block> KHROMA_SEPARATOR = registerBlock("khroma_separator", KhromaSeparatorBlock::new, khromaDeviceProps(true));

	public static final DeferredBlock<Block> KHROMA_DISSIPATOR = registerBlock("khroma_dissipator", KhromaDissipatorBlock::new, khromaDeviceProps());

	public static final DeferredBlock<Block> KHROMA_IMBUER = registerBlock("khroma_imbuer", KhromaImbuerBlock::new, khromaDeviceProps());

	public static final Map<KhromaDeviceTier, DeferredBlock<Block>> KHROMA_PROCESSING_CORE_TIERED = registerTieredDevice("khroma_processing_core", KhromaProcessingCoreBlock::new,
			KhromaDeviceTier.BASIC);

	public static final Supplier<BlockEntityType<KhromaApertureBlockEntity>> BE_KHROMA_APERTURE = BLOCK_ENTITY_TYPES.register("khroma_aperture",
			() -> (new BlockEntityType<>(KhromaApertureBlockEntity::new, KHROMA_APERTURE.get())));

	public static final Supplier<BlockEntityType<KhromaImbuerBlockEntity>> BE_KHROMA_IMBUER = BLOCK_ENTITY_TYPES.register("khroma_imbuer",
			() -> (new BlockEntityType<>(KhromaImbuerBlockEntity::new, KHROMA_IMBUER.get())));

	public static final Supplier<BlockEntityType<KhromaProcessingCoreBlockEntity>> BE_KHROMA_PROCESSING_CORE = BLOCK_ENTITY_TYPES.register("khroma_processing_core",
			() -> (new BlockEntityType<>(KhromaProcessingCoreBlockEntity::new, getAllTieredBlocks(KHROMA_PROCESSING_CORE_TIERED))));

	private static BlockBehaviour.Properties blockProps() {
		return BlockBehaviour.Properties.of();
	}

	private static BlockBehaviour.Properties metalBlockProps(MapColor color) {
		return blockProps().mapColor(color).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL);
	}

	private static BlockBehaviour.Properties khromaDeviceProps(boolean small) {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(small ? 1.0f : 3.0f, small ? 2.0f : 3.5f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK);
	}

	private static BlockBehaviour.Properties khromaDeviceProps() {
		return khromaDeviceProps(false);
	}

	public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties,
			@Nullable BiFunction<Block, Item.Properties, ? extends Item> itemFactory, @Nullable Item.Properties itemProperties) {
		var block = BLOCKS.register(name, registryName -> factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, registryName))));
		if (itemFactory != null)
			ItemReference.ITEMS.register(name, registryName -> itemFactory.apply(block.get(), itemProperties.setId(ResourceKey.create(Registries.ITEM, registryName)).useBlockDescriptionPrefix()));
		else
			ItemReference.ITEMS.registerSimpleBlockItem(block);
		return block;
	}

	public static <T extends Block> DeferredBlock<T> registerBlockNoItem(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		return BLOCKS.register(name, registryName -> factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, registryName))));
	}

	public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		return registerBlock(name, factory, properties, null, null);
	}

	private static <T extends Block> Map<KhromaDeviceTier, DeferredBlock<T>> registerTieredDevice(String baseName, BiFunction<KhromaDeviceTier, BlockBehaviour.Properties, T> factory,
			KhromaDeviceTier fromTier) {
		Map<KhromaDeviceTier, DeferredBlock<T>> result = new HashMap<KhromaDeviceTier, DeferredBlock<T>>();
		var tierValues = KhromaDeviceTier.values();
		for (int i = fromTier.ordinal(); i < tierValues.length; i++) {
			var tier = tierValues[i];
			result.put(tier, registerBlock(tier.getPrefix() + "_" + baseName, properties -> factory.apply(tier, properties), khromaDeviceProps()));
		}

		return result;
	}

	private static <T extends Block> Set<T> getAllTieredBlocks(Map<KhromaDeviceTier, DeferredBlock<T>> map) {
		return Set.copyOf(map.values().stream().map(d -> d.get()).toList());
	}

	public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return true;
	}

	public static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return false;
	}

	private static void setFlammable(Block block, int encouragement, int flammability) {
		((FireBlock) Blocks.FIRE).setFlammable(block, encouragement, flammability);
	}

	@SuppressWarnings("deprecation")
	private static void setCompostable(ItemLike item, float chance) {
		ComposterBlock.COMPOSTABLES.put(item, chance);
	}

	public static void configureExtra() {
		SurgeofKhroma.LOGGER.info("configured");
		setCompostable(SPARKTREE.get(TreeBlock.LEAVES), 0.3f);
		setCompostable(SPARKTREE.get(TreeBlock.SAPLING), 0.3f);

		setCompostable(BLOOMTREE.get(TreeBlock.LEAVES), 1f);
		setCompostable(BLOOMTREE.get(TreeBlock.SAPLING), 1f);

		setFlammable(BLOOMTREE.get(TreeBlock.LOG), 5, 5);
		setFlammable(BLOOMTREE.get(TreeBlock.WOOD), 5, 5);
		setFlammable(BLOOMTREE.get(TreeBlock.STRIPPED_LOG), 5, 5);
		setFlammable(BLOOMTREE.get(TreeBlock.STRIPPED_WOOD), 5, 5);
		setFlammable(BLOOMTREE.get(TreeBlock.LEAVES), 30, 60);
		setFlammable(BLOOMTREE.get(TreeBlock.PLANKS), 5, 20);
		setFlammable(BLOOMTREE.get(TreeBlock.FENCE), 5, 20);
		setFlammable(BLOOMTREE.get(TreeBlock.FENCE_GATE), 5, 20);
		setFlammable(BLOOMTREE.get(TreeBlock.SLAB), 5, 20);
		setFlammable(BLOOMTREE.get(TreeBlock.STAIRS), 5, 20);
	}
}
