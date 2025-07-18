package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.CuriosResources;

@EventBusSubscriber(modid = SurgeofKhroma.MODID)
public class DataGen {

	private static class BLootProvider extends BlockLootSubProvider {

		protected BLootProvider(HolderLookup.Provider lookupProvider) {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Stream.concat(DataGenDefinitions.dropsSelfBlocks.stream().map(e -> (Block) e.get()), DataGenDefinitions.dropsOreBlocks.stream().map(e -> (Block) e.get())).toList();
		}

		@Override
		protected void generate() {
			for (var element : DataGenDefinitions.dropsSelfBlocks)
				dropSelf(element.get());
			add(RegistryReference.BLOCK_CHROMIUM_ORE.get(), block -> createOreDrop(block, RegistryReference.ITEM_RAW_CHROMIUM.get()));
			add(RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.get(), block -> createOreDrop(block, RegistryReference.ITEM_RAW_CHROMIUM.get()));
		}

	}

	@SubscribeEvent
	public static void gatherDataClient(GatherDataEvent.Client event) {
		event.createProvider(ModelDataGen::new);
		generateServerData(event);
	}

	@SubscribeEvent
	public static void gatherDataServer(GatherDataEvent.Server event) {
		generateServerData(event);
	}

	private static void generateServerData(GatherDataEvent event) {
		event.createDatapackRegistryObjects(registrySet(), Set.of(SurgeofKhroma.MODID));

		event.createBlockAndItemTags(TagsDataGen.BlockTag::new, TagsDataGen.ItemTag::new);
		event.createProvider(TagsDataGen.DamageTypeTag::new);
		event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(BLootProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

		event.createProvider(RecipeDaraGen.Runner::new);

		event.createProvider((output, lookupProvider) -> new CuriosDataProvider(SurgeofKhroma.MODID, output, lookupProvider) {
			@Override
			public void generate(Provider registries) {
				createSlot("eyes").order(50).icon(SurgeofKhroma.resource("slot/empty_eyes_slot")).addValidator(CuriosResources.resource("tag"));
				createEntities("player").addPlayer().addSlots("eyes");
			}
		});
	}

	private static RegistrySetBuilder registrySet() {
		RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder()
				.add(Registries.DAMAGE_TYPE, DataGen::registerDamageTypes)
				.add(Registries.CONFIGURED_FEATURE, DataGen::registerConfiguredFeatures)
				.add(Registries.PLACED_FEATURE, DataGen::registerPlacedFeatures)
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, DataGen::registerBiomeModifiers);
		return registrySetBuilder;
	}

	private static void registerDamageTypes(BootstrapContext<DamageType> bootstrap) {
		bootstrap.register(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK, new DamageType("surgeofkhroma.red_khrometal_block", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f));
	}

	private static void registerConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> bootstrap) {
		bootstrap.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, SurgeofKhroma.resource("ore_chromium")),
				new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
						List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), RegistryReference.BLOCK_CHROMIUM_ORE.get().defaultBlockState()),
								OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), RegistryReference.BLOCK_DEEPSLATE_CHROMIUM_ORE.get().defaultBlockState())),
						9)));
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
}
