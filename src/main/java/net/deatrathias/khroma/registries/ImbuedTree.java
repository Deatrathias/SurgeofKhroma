package net.deatrathias.khroma.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.PillarBlock;
import net.deatrathias.khroma.blocks.imbuedtrees.FixedTintParticleLeavesBlock;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ImbuedTree {

	private final String name;

	private final Map<TreeBlock, DeferredBlock<Block>> blocks;

	private BlockFamily family;

	private final TagKey<Block> blockLogsTag;

	private final TagKey<Item> itemLogsTag;

	private DeferredItem<Item> boatItem;

	private Supplier<EntityType<? extends AbstractBoat>> boatEntity;

	private DeferredItem<Item> chestBoatItem;

	private Supplier<EntityType<? extends AbstractBoat>> chestBoatEntity;

	private ImbuedTree(String name) {
		this.name = name;
		blocks = new HashMap<ImbuedTree.TreeBlock, DeferredBlock<Block>>();
		var tag = SurgeofKhroma.resource(name + "_logs");
		blockLogsTag = BlockTags.create(tag);
		itemLogsTag = ItemTags.create(tag);
	}

	public Block get(TreeBlock block) {
		return blocks.get(block).get();
	}

	public Holder<Block> getHolder(TreeBlock block) {
		return blocks.get(block);
	}

	public void ifPresent(TreeBlock block, Consumer<Block> func) {
		var value = blocks.get(block);
		if (value != null)
			func.accept(value.get());
	}

	public String getName() {
		return name;
	}

	public BlockFamily getFamily() {
		if (family == null)
			createFamily();
		return family;
	}

	public TagKey<Block> getBlockLogsTag() {
		return blockLogsTag;
	}

	public TagKey<Item> getItemLogsTag() {
		return itemLogsTag;
	}

	public DeferredItem<Item> getBoatItem() {
		return boatItem;
	}

	public Supplier<EntityType<? extends AbstractBoat>> getBoatEntity() {
		return boatEntity;
	}

	public DeferredItem<Item> getChestBoatItem() {
		return chestBoatItem;
	}

	public Supplier<EntityType<? extends AbstractBoat>> getChestBoatEntity() {
		return chestBoatEntity;
	}

	public void setFamily(BlockFamily family) {
		this.family = family;
	}

	private void createFamily() {
		var familybuilder = new BlockFamily.Builder(get(TreeBlock.PLANKS));
		if (blocks.containsKey(TreeBlock.BUTTON))
			familybuilder.button(get(TreeBlock.BUTTON));
		if (blocks.containsKey(TreeBlock.DOOR))
			familybuilder.door(get(TreeBlock.DOOR));
		if (blocks.containsKey(TreeBlock.FENCE))
			familybuilder.fence(get(TreeBlock.FENCE));
		if (blocks.containsKey(TreeBlock.FENCE_GATE))
			familybuilder.fenceGate(get(TreeBlock.FENCE_GATE));
		if (blocks.containsKey(TreeBlock.PRESSURE_PLATE))
			familybuilder.pressurePlate(get(TreeBlock.PRESSURE_PLATE));
		if (blocks.containsKey(TreeBlock.SIGN))
			familybuilder.sign(get(TreeBlock.SIGN), get(TreeBlock.WALL_SIGN));
		if (blocks.containsKey(TreeBlock.SLAB))
			familybuilder.slab(get(TreeBlock.SLAB));
		if (blocks.containsKey(TreeBlock.STAIRS))
			familybuilder.stairs(get(TreeBlock.STAIRS));
		if (blocks.containsKey(TreeBlock.TRAPDOOR))
			familybuilder.trapdoor(get(TreeBlock.TRAPDOOR));
		family = familybuilder.getFamily();
	}

	public static class Builder {
		private String buildName;

		private Map<TreeBlock, BlockBehaviour.Properties> props;

		private MapColor plankColor;

		private float leafParticleChance;

		private int leafColor;

		private TreeGrower treeGrower;

		private Item.Properties boatItemProps;

		private Item.Properties chestBoatItemProps;

		private Function<BlockBehaviour.Properties, Block> logFactory;

		private Function<BlockBehaviour.Properties, Block> strippedLogFactory;

		private BiFunction<TreeGrower, BlockBehaviour.Properties, Block> saplingFactory;

		private Function<BlockBehaviour.Properties, Block> planksFactory;

		private Function<BlockBehaviour.Properties, Block> slabFactory;

		private BiFunction<BlockState, BlockBehaviour.Properties, Block> stairFactory;

		private BiFunction<BlockSetType, BlockBehaviour.Properties, Block> doorFactory;

		private BiFunction<BlockSetType, BlockBehaviour.Properties, Block> trapdoorFactory;

		private Function<BlockBehaviour.Properties, Block> pillarFactory;

		@FunctionalInterface
		public static interface PropertyModifier {
			BlockBehaviour.Properties modify(BlockBehaviour.Properties input);
		}

		public static final PropertyModifier identity = p -> p;

		public static Builder create(String name) {
			return new Builder(name);
		}

		private Builder(String buildName) {
			this.buildName = buildName;
			props = new HashMap<ImbuedTree.TreeBlock, BlockBehaviour.Properties>();
			logFactory = RotatedPillarBlock::new;
			strippedLogFactory = RotatedPillarBlock::new;
			saplingFactory = SaplingBlock::new;
			planksFactory = Block::new;
			slabFactory = SlabBlock::new;
			stairFactory = StairBlock::new;
			doorFactory = DoorBlock::new;
			trapdoorFactory = TrapDoorBlock::new;
			pillarFactory = PillarBlock::new;
		}

		public Builder log(PropertyModifier modifier, MapColor sideColor, MapColor topColor) {
			props.put(TreeBlock.LOG, modifier.modify(logProperties(sideColor, topColor)));
			return this;
		}

		public Builder log(MapColor sideColor, MapColor topColor) {
			return log(identity, sideColor, topColor);
		}

		public Builder wood(PropertyModifier modifier, MapColor color) {
			props.put(TreeBlock.WOOD, modifier.modify(woodProperties(color)));
			return this;
		}

		public Builder wood(MapColor color) {
			return wood(identity, color);
		}

		public Builder strippedLog(PropertyModifier modifier, MapColor sideColor, MapColor topColor) {
			props.put(TreeBlock.STRIPPED_LOG, modifier.modify(logProperties(sideColor, topColor)));
			return this;
		}

		public Builder strippedLog(MapColor sideColor, MapColor topColor) {
			return strippedLog(identity, sideColor, topColor);
		}

		public Builder strippedWood(PropertyModifier modifier, MapColor color) {
			props.put(TreeBlock.STRIPPED_WOOD, modifier.modify(woodProperties(color)));
			return this;
		}

		public Builder strippedWood(MapColor color) {
			return strippedWood(identity, color);
		}

		public Builder leaves(PropertyModifier modifier, SoundType sound, float leafParticleChance, int leafColor) {
			props.put(TreeBlock.LEAVES, modifier.modify(leavesProperties(sound)));
			this.leafParticleChance = leafParticleChance;
			this.leafColor = leafColor;
			return this;
		}

		public Builder leaves(SoundType sound, float leafParticleChance, int leafColor) {
			return leaves(identity, sound, leafParticleChance, leafColor);
		}

		public Builder sapling(PropertyModifier modifier, TreeGrower treeGrower) {
			props.put(TreeBlock.SAPLING, modifier.modify(saplingProperties()));
			this.treeGrower = treeGrower;
			return this;
		}

		public Builder sapling(TreeGrower treeGrower) {
			return sapling(identity, treeGrower);
		}

		public Builder pottedSapling(PropertyModifier modifier) {
			props.put(TreeBlock.POTTED_SAPLING, modifier.modify(flowerPotProperties()));
			return this;
		}

		public Builder pottedSapling() {
			return pottedSapling(identity);
		}

		public Builder planks(PropertyModifier modifier, MapColor color) {
			plankColor = color;
			props.put(TreeBlock.PLANKS, modifier.modify(plankProperties(color)));
			return this;
		}

		public Builder planks(MapColor color) {
			return planks(identity, color);
		}

		public Builder button(PropertyModifier modifier) {
			props.put(TreeBlock.BUTTON, modifier.modify(buttonProperties()));
			return this;
		}

		public Builder button() {
			return button(identity);
		}

		public Builder fence(PropertyModifier modifier) {
			props.put(TreeBlock.FENCE, modifier.modify(plankProperties(plankColor).forceSolidOn()));
			return this;
		}

		public Builder fence() {
			return fence(identity);
		}

		public Builder fenceGate(PropertyModifier modifier) {
			props.put(TreeBlock.FENCE_GATE, modifier.modify(plankProperties(plankColor).forceSolidOn()));
			return this;
		}

		public Builder fenceGate() {
			return fenceGate(identity);
		}

		public Builder sign(PropertyModifier modifier) {
			var location = SurgeofKhroma.resource(buildName + "_sign");
			var key = ResourceKey.create(Registries.LOOT_TABLE, location.withPrefix("blocks/"));
			var properties = modifier.modify(signProperties(plankColor).overrideLootTable(Optional.of(key)).overrideDescription(Util.makeDescriptionId("block", location)));
			props.put(TreeBlock.SIGN, properties);
			props.put(TreeBlock.WALL_SIGN, properties);
			return this;
		}

		public Builder sign() {
			return sign(identity);
		}

		public Builder hangingSign(PropertyModifier modifier) {
			var location = SurgeofKhroma.resource(buildName + "_hanging_sign");
			var key = ResourceKey.create(Registries.LOOT_TABLE, location.withPrefix("blocks/"));
			var properties = modifier.modify(signProperties(plankColor).overrideLootTable(Optional.of(key)).overrideDescription(Util.makeDescriptionId("block", location)));
			props.put(TreeBlock.HANGING_SIGN, properties);
			props.put(TreeBlock.WALL_HANGING_SIGN, properties);
			return this;
		}

		public Builder hangingSign() {
			return hangingSign(identity);
		}

		public Builder slab(PropertyModifier modifier) {
			props.put(TreeBlock.SLAB, modifier.modify(plankProperties(plankColor)));
			return this;
		}

		public Builder slab() {
			slab(identity);
			return this;
		}

		public Builder stairs(PropertyModifier modifier) {
			props.put(TreeBlock.STAIRS, modifier.modify(plankProperties(plankColor)));
			return this;
		}

		public Builder stairs() {
			return stairs(identity);
		}

		public Builder pressurePlate(PropertyModifier modifier) {
			props.put(TreeBlock.PRESSURE_PLATE, modifier.modify(pressurePlateProperties(plankColor)));
			return this;
		}

		public Builder pressurePlate() {
			return pressurePlate(identity);
		}

		public Builder door(PropertyModifier modifier) {
			props.put(TreeBlock.DOOR, modifier.modify(doorProperties(plankColor)));
			return this;
		}

		public Builder door() {
			return door(identity);
		}

		public Builder trapdoor(PropertyModifier modifier) {
			props.put(TreeBlock.TRAPDOOR, modifier.modify(trapdoorProperties(plankColor)));
			return this;
		}

		public Builder trapdoor() {
			return trapdoor(identity);
		}

		public Builder pillar(PropertyModifier modifier) {
			props.put(TreeBlock.PILLAR, modifier.modify(plankProperties(plankColor)));
			return this;
		}

		public Builder pillar() {
			pillar(identity);
			return this;
		}

		public Builder boat(Item.Properties properties) {
			boatItemProps = properties;
			return this;
		}

		public Builder boat() {
			return boat(new Item.Properties().stacksTo(1));
		}

		public Builder chestBoat(Item.Properties properties) {
			chestBoatItemProps = properties;
			return this;
		}

		public Builder chestBoat() {
			return chestBoat(new Item.Properties().stacksTo(1));
		}

		public Builder setLogFactory(Function<BlockBehaviour.Properties, Block> logFactory) {
			this.logFactory = logFactory;
			return this;
		}

		public Builder setStrippedLogFactory(Function<BlockBehaviour.Properties, Block> strippedLogFactory) {
			this.strippedLogFactory = strippedLogFactory;
			return this;
		}

		public Builder setSaplingFactory(BiFunction<TreeGrower, BlockBehaviour.Properties, Block> saplingFactory) {
			this.saplingFactory = saplingFactory;
			return this;
		}

		public Builder setPlanksFactory(Function<BlockBehaviour.Properties, Block> planksFactory) {
			this.planksFactory = planksFactory;
			return this;
		}

		public Builder setSlabFactory(Function<BlockBehaviour.Properties, Block> slabFactory) {
			this.slabFactory = slabFactory;
			return this;
		}

		public Builder setStairFactory(BiFunction<BlockState, BlockBehaviour.Properties, Block> stairFactory) {
			this.stairFactory = stairFactory;
			return this;
		}

		public Builder setDoorFactory(BiFunction<BlockSetType, BlockBehaviour.Properties, Block> doorFactory) {
			this.doorFactory = doorFactory;
			return this;
		}

		public Builder setTrapdoorFactory(BiFunction<BlockSetType, BlockBehaviour.Properties, Block> trapdoorFactory) {
			this.trapdoorFactory = trapdoorFactory;
			return this;
		}

		public Builder setPillarFactory(Function<BlockBehaviour.Properties, Block> pillarFactory) {
			this.pillarFactory = pillarFactory;
			return this;
		}

		@SuppressWarnings("deprecation")
		public ImbuedTree build(BlockSetType blockSet, WoodType woodType) {
			ImbuedTree result = new ImbuedTree(buildName);

			for (var key : TreeBlock.values()) {
				var prop = props.get(key);
				if (prop == null)
					continue;
				DeferredBlock<Block> block = null;

				switch (key) {
				case LOG:
					block = BlockReference.registerBlock(buildName + "_log", logFactory, prop);
					break;
				case WOOD:
					block = BlockReference.registerBlock(buildName + "_wood", logFactory, prop);
					break;
				case STRIPPED_LOG:
					block = BlockReference.registerBlock("stripped_" + buildName + "_log", strippedLogFactory, prop);
					break;
				case STRIPPED_WOOD:
					block = BlockReference.registerBlock("stripped_" + buildName + "_wood", strippedLogFactory, prop);
					break;
				case LEAVES:
					block = BlockReference.registerBlock(buildName + "_leaves", properties -> new FixedTintParticleLeavesBlock(leafParticleChance, leafColor, properties), prop);
					break;
				case SAPLING:
					block = BlockReference.registerBlock(buildName + "_sapling", properties -> saplingFactory.apply(treeGrower, properties), prop);
					break;
				case POTTED_SAPLING:
					block = BlockReference.registerBlockNoItem("potted_" + buildName + "_sapling", properties -> new FlowerPotBlock(result.get(TreeBlock.SAPLING), properties),
							prop);
					break;
				case PLANKS:
					block = BlockReference.registerBlock(buildName + "_planks", planksFactory, prop);
					break;
				case BUTTON:
					block = BlockReference.registerBlock(buildName + "_button", properties -> new ButtonBlock(blockSet, 30, properties), prop);
					break;
				case FENCE:
					block = BlockReference.registerBlock(buildName + "_fence", FenceBlock::new, prop);
					break;
				case FENCE_GATE:
					block = BlockReference.registerBlock(buildName + "_fence_gate", properties -> new FenceGateBlock(woodType, properties), prop);
					break;
				case SIGN:
					block = BlockReference.registerBlock(buildName + "_sign", properties -> new StandingSignBlock(woodType, properties), prop,
							(itemBlock, properties) -> new SignItem(itemBlock, result.get(TreeBlock.WALL_SIGN), properties),
							new Item.Properties().stacksTo(16));
					break;
				case WALL_SIGN:
					block = BlockReference.registerBlockNoItem(buildName + "_wall_sign", properties -> new WallSignBlock(woodType, properties), prop);
					break;
				case HANGING_SIGN:
					block = BlockReference.registerBlock(buildName + "_hanging_sign", properties -> new CeilingHangingSignBlock(woodType, properties), prop,
							(itemBlock, properties) -> new HangingSignItem(itemBlock, result.get(TreeBlock.WALL_HANGING_SIGN), properties), new Item.Properties().stacksTo(16));
					break;
				case WALL_HANGING_SIGN:
					block = BlockReference.registerBlockNoItem(buildName + "_wall_hanging_sign", properties -> new WallHangingSignBlock(woodType, properties), prop);
					break;
				case SLAB:
					block = BlockReference.registerBlock(buildName + "_slab", slabFactory, prop);
					break;
				case STAIRS:
					block = BlockReference.registerBlock(buildName + "_stairs", properties -> stairFactory.apply(result.get(TreeBlock.PLANKS).defaultBlockState(), properties), prop);
					break;
				case PRESSURE_PLATE:
					block = BlockReference.registerBlock(buildName + "_pressure_plate", properties -> new PressurePlateBlock(blockSet, properties), prop);
					break;
				case DOOR:
					block = BlockReference.registerBlock(buildName + "_door", properties -> doorFactory.apply(blockSet, properties), prop);
					break;
				case TRAPDOOR:
					block = BlockReference.registerBlock(buildName + "_trapdoor", properties -> trapdoorFactory.apply(blockSet, properties), prop);
					break;
				case PILLAR:
					block = BlockReference.registerBlock(buildName + "_pillar", pillarFactory, prop);
					break;
				}

				result.blocks.put(key, block);
			}

			if (boatItemProps != null) {
				result.boatEntity = EntityReference.ENTITY_TYPES.register(buildName + "_boat",
						registryName -> EntityType.Builder.<Boat>of((entityType, level) -> new Boat(entityType, level, result.getBoatItem()), MobCategory.MISC)
								.noLootTable()
								.sized(1.375F, 0.5625F)
								.eyeHeight(0.5625F)
								.clientTrackingRange(10)
								.build(ResourceKey.create(Registries.ENTITY_TYPE, registryName)));
				result.boatItem = ItemReference.ITEMS.register(buildName + "_boat",
						registryName -> new BoatItem(result.boatEntity.get(), boatItemProps.setId(ResourceKey.create(Registries.ITEM, registryName))));

				if (chestBoatItemProps != null) {
					result.chestBoatEntity = EntityReference.ENTITY_TYPES.register(buildName + "_chest_boat",
							registryName -> EntityType.Builder.<ChestBoat>of((entityType, level) -> new ChestBoat(entityType, level, result.getChestBoatItem()), MobCategory.MISC)
									.noLootTable()
									.sized(1.375F, 0.5625F)
									.eyeHeight(0.5625F)
									.clientTrackingRange(10)
									.build(ResourceKey.create(Registries.ENTITY_TYPE, registryName)));
					result.chestBoatItem = ItemReference.ITEMS.register(buildName + "_chest_boat",
							registryName -> new BoatItem(result.chestBoatEntity.get(), chestBoatItemProps.setId(ResourceKey.create(Registries.ITEM, registryName))));
				}
			}

			return result;
		}
	}

	private static BlockBehaviour.Properties sharedWoodProperties() {
		return BlockBehaviour.Properties.of()
				.instrument(NoteBlockInstrument.BASS)
				.strength(2.0F)
				.sound(SoundType.WOOD)
				.ignitedByLava();
	}

	private static BlockBehaviour.Properties logProperties(MapColor sideColor, MapColor topColor) {
		return sharedWoodProperties().mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? sideColor : topColor);
	}

	private static BlockBehaviour.Properties woodProperties(MapColor color) {
		return sharedWoodProperties().mapColor(color);
	}

	private static BlockBehaviour.Properties leavesProperties(SoundType sound) {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT)
				.strength(0.2F)
				.randomTicks()
				.sound(sound)
				.noOcclusion()
				.isValidSpawn(Blocks::ocelotOrParrot)
				.isSuffocating(BlockReference::never)
				.isViewBlocking(BlockReference::never)
				.ignitedByLava()
				.pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor(BlockReference::never);
	}

	private static BlockBehaviour.Properties saplingProperties() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT)
				.noCollision()
				.randomTicks()
				.instabreak()
				.sound(SoundType.GRASS)
				.pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties flowerPotProperties() {
		return BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties plankProperties(MapColor color, float destroyTime, float explosionResistance) {
		return BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASS).strength(destroyTime, explosionResistance).sound(SoundType.WOOD).ignitedByLava();
	}

	private static BlockBehaviour.Properties plankProperties(MapColor color) {
		return plankProperties(color, 2f, 3f);
	}

	private static BlockBehaviour.Properties buttonProperties() {
		return BlockBehaviour.Properties.of().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties signProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.forceSolidOn()
				.instrument(NoteBlockInstrument.BASS)
				.noCollision()
				.strength(1.0F)
				.ignitedByLava();
	}

	private static BlockBehaviour.Properties pressurePlateProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.forceSolidOn()
				.instrument(NoteBlockInstrument.BASS)
				.noCollision()
				.strength(0.5F)
				.ignitedByLava()
				.pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties doorProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.instrument(NoteBlockInstrument.BASS)
				.strength(3.0F)
				.noOcclusion()
				.ignitedByLava()
				.pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties trapdoorProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.instrument(NoteBlockInstrument.BASS)
				.strength(3.0F)
				.noOcclusion()
				.isValidSpawn(Blocks::never)
				.ignitedByLava();
	}

	public enum TreeBlock {
		LOG,
		WOOD,
		STRIPPED_LOG,
		STRIPPED_WOOD,
		LEAVES,
		SAPLING,
		POTTED_SAPLING,
		PLANKS,
		BUTTON,
		FENCE,
		FENCE_GATE,
		SIGN,
		WALL_SIGN,
		HANGING_SIGN,
		WALL_HANGING_SIGN,
		SLAB,
		STAIRS,
		PRESSURE_PLATE,
		DOOR,
		TRAPDOOR,
		PILLAR
	}
}
