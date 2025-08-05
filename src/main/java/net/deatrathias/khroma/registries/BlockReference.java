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
import net.deatrathias.khroma.blocks.imbuedtrees.FlowtreeSaplingBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimDoorBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimImbuedLogBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimPillarBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimRotatedPillarBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimSimpleBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimSlabBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimStairBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.GrimTrapDoor;
import net.deatrathias.khroma.blocks.imbuedtrees.SkyImbuedLogBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.SkyPillarBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.SkyRotatedPillarBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.SkySimpleBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.SkySlabBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.SkyStairBlock;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
			.pillar()
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
			.pillar(p -> p.strength(3f, 6f))
			.build(BlockSetReference.BLOOMTREE, WoodReference.BLOOMTREE);

	public static final ImbuedTree FLOWTREE = ImbuedTree.Builder.create("flowtree")
			.log(p -> p.friction(0.98f), MapColor.TERRACOTTA_BLUE, MapColor.COLOR_BLUE)
			.wood(p -> p.friction(0.98f), MapColor.TERRACOTTA_BLUE)
			.strippedLog(p -> p.friction(0.98f), MapColor.COLOR_BLUE, MapColor.COLOR_BLUE)
			.strippedWood(p -> p.friction(0.98f), MapColor.COLOR_BLUE)
			.leaves(SoundType.GRASS, 0.01f, 0xff2121e4)
			.sapling(ImbuedTree.Builder.identity, TreeGrowerReference.FLOWTREE)
			.setSaplingFactory(FlowtreeSaplingBlock::new)
			.pottedSapling()
			.planks(p -> p.friction(0.98f), MapColor.COLOR_BLUE)
			.button()
			.fence(p -> p.friction(0.98f))
			.fenceGate(p -> p.friction(0.98f))
			.slab(p -> p.friction(0.98f))
			.stairs(p -> p.friction(0.98f))
			.pressurePlate()
			.door(p -> p.friction(0.98f))
			.trapdoor(p -> p.friction(0.98f))
			.sign()
			.hangingSign()
			.boat()
			.chestBoat()
			.pillar(p -> p.friction(0.98f))
			.build(BlockSetReference.FLOWTREE, WoodReference.FLOWTREE);

	public static final ImbuedTree SKYTREE = ImbuedTree.Builder.create("skytree")
			.log(MapColor.TERRACOTTA_WHITE, MapColor.QUARTZ)
			.wood(MapColor.TERRACOTTA_WHITE)
			.setLogFactory(SkyImbuedLogBlock::new)
			.strippedLog(MapColor.QUARTZ, MapColor.QUARTZ)
			.strippedWood(MapColor.QUARTZ)
			.setStrippedLogFactory(SkyRotatedPillarBlock::new)
			.leaves(SoundType.GRASS, 0.01f, 0xfffbfbfb)
			.sapling(TreeGrowerReference.SKYTREE)
			.pottedSapling()
			.planks(MapColor.QUARTZ)
			.setPlanksFactory(SkySimpleBlock::new)
			.button()
			.fence()
			.fenceGate()
			.slab()
			.setSlabFactory(SkySlabBlock::new)
			.stairs()
			.setStairFactory(SkyStairBlock::new)
			.pressurePlate()
			.door()
			.trapdoor()
			.sign()
			.hangingSign()
			.boat()
			.chestBoat()
			.pillar()
			.setPillarFactory(SkyPillarBlock::new)
			.build(BlockSetReference.SKYTREE, WoodReference.SKYTREE);

	public static final ImbuedTree GRIMTREE = ImbuedTree.Builder.create("grimtree")
			.log(MapColor.COLOR_GRAY, MapColor.COLOR_BLACK)
			.wood(MapColor.COLOR_GRAY)
			.setLogFactory(GrimImbuedLogBlock::new)
			.strippedLog(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK)
			.strippedWood(MapColor.COLOR_BLACK)
			.setStrippedLogFactory(GrimRotatedPillarBlock::new)
			.leaves(SoundType.GRASS, 0.01f, 0xff414141)
			.sapling(TreeGrowerReference.GRIMTREE)
			.pottedSapling()
			.planks(MapColor.COLOR_BLACK)
			.setPlanksFactory(GrimSimpleBlock::new)
			.button()
			.fence()
			.fenceGate()
			.slab()
			.setSlabFactory(GrimSlabBlock::new)
			.stairs()
			.setStairFactory(GrimStairBlock::new)
			.pressurePlate()
			.door()
			.setDoorFactory(GrimDoorBlock::new)
			.trapdoor()
			.setTrapdoorFactory(GrimTrapDoor::new)
			.sign()
			.hangingSign()
			.boat()
			.chestBoat()
			.pillar()
			.setPillarFactory(GrimPillarBlock::new)
			.build(BlockSetReference.GRIMTREE, WoodReference.GRIMTREE);

	public static final List<ImbuedTree> IMBUED_TREES = List.of(SPARKTREE, BLOOMTREE, FLOWTREE, SKYTREE, GRIMTREE);

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

	public static void configureExtra() {
		for (var tree : IMBUED_TREES) {
			if (tree != SPARKTREE) {
				setFlammable(tree.get(TreeBlock.LOG), 5, 5);
				setFlammable(tree.get(TreeBlock.WOOD), 5, 5);
				setFlammable(tree.get(TreeBlock.STRIPPED_LOG), 5, 5);
				setFlammable(tree.get(TreeBlock.STRIPPED_WOOD), 5, 5);
				setFlammable(tree.get(TreeBlock.LEAVES), 30, 60);
				setFlammable(tree.get(TreeBlock.PLANKS), 5, 20);
				setFlammable(tree.get(TreeBlock.FENCE), 5, 20);
				setFlammable(tree.get(TreeBlock.FENCE_GATE), 5, 20);
				setFlammable(tree.get(TreeBlock.SLAB), 5, 20);
				setFlammable(tree.get(TreeBlock.STAIRS), 5, 20);
				tree.ifPresent(TreeBlock.PILLAR, block -> setFlammable(block, 5, 20));
			}
		}
	}
}
