package net.deatrathias.khroma.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.TagReference;
import net.deatrathias.khroma.registries.TreeGrowerReference;
import net.deatrathias.khroma.worldgen.BloomtreeTrunkPlacer;
import net.deatrathias.khroma.worldgen.PlaceFlowersDecorator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLogsDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RegistrySetDataGen {

	public static RegistrySetBuilder registrySet() {
		RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder()
				.add(Registries.DAMAGE_TYPE, RegistrySetDataGen::registerDamageTypes)
				.add(Registries.CONFIGURED_FEATURE, RegistrySetDataGen::registerConfiguredFeatures)
				.add(Registries.PLACED_FEATURE, RegistrySetDataGen::registerPlacedFeatures)
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RegistrySetDataGen::registerBiomeModifiers)
				.add(Registries.ENCHANTMENT, RegistrySetDataGen::registerEnchantments);
		return registrySetBuilder;
	}

	private static void registerDamageTypes(BootstrapContext<DamageType> bootstrap) {
		bootstrap.register(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK, new DamageType("surgeofkhroma.red_khrometal_block", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f));
	}

	private static void registerConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> bootstrap) {
		HolderGetter<Block> blockLookup = bootstrap.lookup(Registries.BLOCK);

		bootstrap.register(SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "ore_chromium"),
				new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
						List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), BlockReference.CHROMIUM_ORE.get().defaultBlockState()),
								OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), BlockReference.DEEPSLATE_CHROMIUM_ORE.get().defaultBlockState())),
						9)));

		var treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.SPARKTREE.get(TreeBlock.LOG)),
				new CherryTrunkPlacer(5, 1, 0, ConstantInt.of(3), ConstantInt.of(2), UniformInt.of(-2, -1), UniformInt.of(-1, 0)),
				BlockStateProvider.simple(BlockReference.SPARKTREE.get(TreeBlock.LEAVES)),
				new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, ConstantInt.of(3)),
				new TwoLayersFeatureSize(2, 0, 2))
				.decorators(List.of(new PlaceOnGroundDecorator(128, 3, 1, BlockStateProvider.simple(Blocks.FIRE))))
				.ignoreVines().build();
		bootstrap.register(TreeGrowerReference.FEATURE_SPARKTREE, new ConfiguredFeature<>(Feature.TREE, treeConfiguration));

		treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.BLOOMTREE.get(TreeBlock.LOG)),
				new BloomtreeTrunkPlacer(11, 4, 2, 0.5f, UniformInt.of(3, 5), UniformInt.of(2, 5)),
				BlockStateProvider.simple(BlockReference.BLOOMTREE.get(TreeBlock.LEAVES)),
				new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
				new TwoLayersFeatureSize(1, 1, 2))
				.ignoreVines()
				.decorators(List.of(new LeaveVineDecorator(0.9f),
						new AlterGroundDecorator(BlockStateProvider.simple(Blocks.GRASS_BLOCK)),
						new PlaceFlowersDecorator(128, 5, 1)))
				.build();
		bootstrap.register(TreeGrowerReference.FEATURE_BLOOMTREE, new ConfiguredFeature<>(Feature.TREE, treeConfiguration));

		treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.FLOWTREE.get(TreeBlock.LOG)),
				new UpwardsBranchingTrunkPlacer(7, 2, 0, ConstantInt.of(5), 0.5f, UniformInt.of(2, 4), HolderSet.direct(BlockReference.FLOWTREE.getHolder(TreeBlock.LEAVES))),
				BlockStateProvider.simple(BlockReference.FLOWTREE.get(TreeBlock.LEAVES)),
				new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), ConstantInt.of(2), 40),
				Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3), BlockStateProvider.simple(BlockReference.FLOWTREE.get(TreeBlock.LOG)), Optional.empty(),
						new MangroveRootPlacement(blockLookup.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.empty(), BlockStateProvider.simple(Blocks.AIR), 12, 10,
								0.2f))),
				new TwoLayersFeatureSize(2, 0, 2))
				.decorators(List.of(new AttachedToLeavesDecorator(0.1f, 2, 3, BlockStateProvider.simple(Blocks.WATER), 1, List.of(Direction.values()))))
				.ignoreVines()
				.build();
		bootstrap.register(TreeGrowerReference.FEATURE_FLOWTREE, new ConfiguredFeature<>(Feature.TREE, treeConfiguration));

		List<TreeDecorator> lichenAttachments = new ArrayList<TreeDecorator>(4);
		for (var dir : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
			lichenAttachments.add(new AttachedToLogsDecorator(0.5f,
					BlockStateProvider.simple(Blocks.GLOW_LICHEN.defaultBlockState().setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir.getOpposite()), true)), List.of(dir)));
		}

		treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.SKYTREE.get(TreeBlock.LOG)),
				new StraightTrunkPlacer(16, 4, 2),
				BlockStateProvider.simple(BlockReference.SKYTREE.get(TreeBlock.LEAVES)),
				new SpruceFoliagePlacer(UniformInt.of(2, 4), ConstantInt.of(2), ConstantInt.of(15)),
				new TwoLayersFeatureSize(8, 0, 1))
				.decorators(lichenAttachments)
				.ignoreVines()
				.build();
		bootstrap.register(TreeGrowerReference.FEATURE_SKYTREE, new ConfiguredFeature<>(Feature.TREE, treeConfiguration));

		treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.GRIMTREE.get(TreeBlock.LOG)),
				new ForkingTrunkPlacer(5, 2, 2),
				BlockStateProvider.simple(BlockReference.GRIMTREE.get(TreeBlock.LEAVES)),
				new MegaJungleFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), 1),
				new TwoLayersFeatureSize(2, 1, 2))
				.decorators(List.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.MUD)),
						new AttachedToLeavesDecorator(0.2f, 3, 1, BlockStateProvider.simple(Blocks.COBWEB), 1, List.of(Direction.DOWN))))
				.ignoreVines()
				.build();
		bootstrap.register(TreeGrowerReference.FEATURE_GRIMTREE, new ConfiguredFeature<>(Feature.TREE, treeConfiguration));
	}

	private static void registerPlacedFeatures(BootstrapContext<PlacedFeature> bootstrap) {
		ResourceKey<ConfiguredFeature<?, ?>> configured = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "ore_chromium");
		bootstrap.register(SurgeofKhroma.resourceKey(Registries.PLACED_FEATURE, "ore_chromium"),
				new PlacedFeature(
						bootstrap.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configured),
						List.of(CountPlacement.of(10),
								InSquarePlacement.spread(),
								HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(90)),
								BiomeFilter.biome())));
	}

	private static void registerBiomeModifiers(BootstrapContext<BiomeModifier> bootstrap) {
		var biomes = bootstrap.lookup(Registries.BIOME);
		var placedFeatures = bootstrap.lookup(Registries.PLACED_FEATURE);
		var placekey = SurgeofKhroma.resourceKey(Registries.PLACED_FEATURE, "ore_chromium");
		bootstrap.register(SurgeofKhroma.resourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, "ore_chromium"),
				new AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
						HolderSet.direct(placedFeatures.getOrThrow(placekey)),
						Decoration.UNDERGROUND_ORES));
	}

	private static void registerEnchantments(BootstrapContext<Enchantment> bootstrap) {
		var itemLookup = bootstrap.lookup(Registries.ITEM);
		var enchanmentLookup = bootstrap.lookup(Registries.ENCHANTMENT);
		var entityTypeLookup = bootstrap.lookup(Registries.ENTITY_TYPE);
		var key = SurgeofKhroma.resourceKey(Registries.ENCHANTMENT, "featherclip");
		bootstrap.register(key,
				Enchantment.enchantment(Enchantment.definition(itemLookup.getOrThrow(ItemTags.WEAPON_ENCHANTABLE), itemLookup.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						5, 5, Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 2, EquipmentSlotGroup.MAINHAND))
						.exclusiveWith(enchanmentLookup.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
						.withEffect(EnchantmentEffectComponents.DAMAGE,
								new AddValue(LevelBasedValue.perLevel(2.5f)),
								LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
										EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeLookup, TagReference.Entities.SENSITIVE_TO_FEATHERCLIP))))
						.withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
								new ApplyMobEffect(HolderSet.direct(RegistryReference.EFFECT_PULL_DOWN),
										LevelBasedValue.perLevel(2),
										LevelBasedValue.perLevel(2),
										LevelBasedValue.constant(0),
										LevelBasedValue.constant(0)))
						.build(key.location()));
	}
}
