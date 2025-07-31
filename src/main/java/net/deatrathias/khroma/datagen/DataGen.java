package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
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
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import top.theillusivec4.curios.api.CuriosResources;

@EventBusSubscriber(modid = SurgeofKhroma.MODID)
public class DataGen {

	@SubscribeEvent
	public static void gatherDataClient(GatherDataEvent.Client event) {
		DataGenDefinitions.init();
		event.createProvider(ModelDataGen::new);
		event.createProvider(output -> equipmentProvider(output));
		generateServerData(event);
	}

	@SubscribeEvent
	public static void gatherDataServer(GatherDataEvent.Server event) {
		DataGenDefinitions.init();
		generateServerData(event);
	}

	private static void generateServerData(GatherDataEvent event) {
		event.createDatapackRegistryObjects(registrySet(), Set.of(SurgeofKhroma.MODID));

		event.createProvider(TagsDataGen.BlockTag::new);
		event.createProvider(TagsDataGen.ItemTag::new);
		event.createProvider(TagsDataGen.DamageTypeTag::new);
		event.createProvider(TagsDataGen.EntityTypeTag::new);
		event.createProvider(TagsDataGen.EnchantmentTag::new);
		event.createProvider(LootTableDataGen::new);
		event.createProvider(AdvancementDataGen::new);

		event.createProvider(RecipeDaraGen.Runner::new);
		event.createProvider(DataMapDataGen::new);
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			event.createProvider(CuriosDataGen::new);
	}

	private static RegistrySetBuilder registrySet() {
		RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder()
				.add(Registries.DAMAGE_TYPE, DataGen::registerDamageTypes)
				.add(Registries.CONFIGURED_FEATURE, DataGen::registerConfiguredFeatures)
				.add(Registries.PLACED_FEATURE, DataGen::registerPlacedFeatures)
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, DataGen::registerBiomeModifiers)
				.add(Registries.ENCHANTMENT, DataGen::registerEnchantments);
		return registrySetBuilder;
	}

	private static void registerDamageTypes(BootstrapContext<DamageType> bootstrap) {
		bootstrap.register(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK, new DamageType("surgeofkhroma.red_khrometal_block", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f));
	}

	private static void registerConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> bootstrap) {
		bootstrap.register(SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "ore_chromium"),
				new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
						List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), BlockReference.CHROMIUM_ORE.get().defaultBlockState()),
								OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), BlockReference.DEEPSLATE_CHROMIUM_ORE.get().defaultBlockState())),
						9)));

		var treeConfiguration = new TreeConfigurationBuilder(
				BlockStateProvider.simple(BlockReference.SPARKTREE.get(TreeBlock.LOG)),
				new CherryTrunkPlacer(4, 2, 0, ConstantInt.of(3), ConstantInt.of(2), UniformInt.of(-2, -1), UniformInt.of(-1, 0)),
				BlockStateProvider.simple(BlockReference.SPARKTREE.get(TreeBlock.LEAVES)),
				new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, ConstantInt.of(3)),
				new TwoLayersFeatureSize(2, 0, 2))
				.decorators(List.of(new PlaceOnGroundDecorator(128, 3, 1, BlockStateProvider.simple(Blocks.FIRE))))
				.ignoreVines().build();
		bootstrap.register(SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "sparktree"), new ConfiguredFeature<>(Feature.TREE, treeConfiguration));
	}

	private static void registerPlacedFeatures(BootstrapContext<PlacedFeature> bootstrap) {
		ResourceKey<ConfiguredFeature<?, ?>> configured = ResourceKey.create(Registries.CONFIGURED_FEATURE, SurgeofKhroma.resource("ore_chromium"));
		bootstrap.register(ResourceKey.create(Registries.PLACED_FEATURE, SurgeofKhroma.resource("ore_chromium")),
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
		var placekey = ResourceKey.create(Registries.PLACED_FEATURE, SurgeofKhroma.resource("ore_chromium"));
		bootstrap.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SurgeofKhroma.resource("ore_chromium")),
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
										EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeLookup, TagReference.ENTITY_SENSITIVE_TO_FEATHERCLIP))))
						.withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM,
								new ApplyMobEffect(HolderSet.direct(RegistryReference.EFFECT_PULL_DOWN),
										LevelBasedValue.perLevel(2),
										LevelBasedValue.perLevel(2),
										LevelBasedValue.constant(0),
										LevelBasedValue.constant(0)))
						.build(key.location()));
	}

	private static DataProvider equipmentProvider(PackOutput output) {
		return new EquipmentAssetProvider(output) {
			@Override
			protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
				output.accept(SurgeofKhroma.resourceKey(EquipmentAssets.ROOT_ID, "chromatic_glasses"), EquipmentClientInfo.builder()
						.addMainHumanoidLayer(SurgeofKhroma.resource("chromatic_glasses"), false)
						.build());
			}
		};
	}

	private static class DataMapDataGen extends DataMapProvider {

		protected DataMapDataGen(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
			super(packOutput, lookupProvider);
		}

		@Override
		protected void gather(Provider provider) {
			builder(NeoForgeDataMaps.COMPOSTABLES)
					.add(TagReference.ITEM_IMBUED_TREE_LEAVES, new Compostable(0.3f), false)
					.add(TagReference.ITEM_IMBUED_TREE_SAPLINGS, new Compostable(0.3f), false);
		}
	}
}
