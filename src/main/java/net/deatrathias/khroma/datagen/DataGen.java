package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosDataProvider;

@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.MOD)
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
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeClient(), new BlockStateProvider(output, SurgeofKhroma.MODID, fileHelper) {
			@Override
			protected void registerStatesAndModels() {
				for (var element : DataGenDefinitions.simpleBlocks) {
					VariantBlockStateBuilder variant = getVariantBuilder(element.get());
					variant.forAllStates(
							(state) -> new ConfiguredModel[] { new ConfiguredModel(new ModelFile.ExistingModelFile(SurgeofKhroma.resource("block/" + element.getId().getPath()), fileHelper)) });
				}

				for (var element : DataGenDefinitions.horDirectionBlocks) {
					VariantBlockStateBuilder variant = getVariantBuilder(element.get());
					ModelFile modelFile = new ModelFile.ExistingModelFile(SurgeofKhroma.resource("block/" + element.getId().getPath()), fileHelper);
					variant.addModels(variant.partialState().with(HorizontalDirectionalBlock.FACING, Direction.NORTH), new ConfiguredModel(modelFile, 0, 0, false));
					variant.addModels(variant.partialState().with(HorizontalDirectionalBlock.FACING, Direction.EAST), new ConfiguredModel(modelFile, 0, 90, false));
					variant.addModels(variant.partialState().with(HorizontalDirectionalBlock.FACING, Direction.SOUTH), new ConfiguredModel(modelFile, 0, 180, false));
					variant.addModels(variant.partialState().with(HorizontalDirectionalBlock.FACING, Direction.WEST), new ConfiguredModel(modelFile, 0, 270, false));
				}

				for (var element : DataGenDefinitions.fullDirectionBlocks) {
					VariantBlockStateBuilder variant = getVariantBuilder(element.get());
					ModelFile modelFile = new ModelFile.ExistingModelFile(SurgeofKhroma.resource("block/" + element.getId().getPath()), fileHelper);
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.NORTH), new ConfiguredModel(modelFile, 0, 0, false));
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.EAST), new ConfiguredModel(modelFile, 0, 90, false));
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.SOUTH), new ConfiguredModel(modelFile, 0, 180, false));
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.WEST), new ConfiguredModel(modelFile, 0, 270, false));
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.UP), new ConfiguredModel(modelFile, 270, 0, false));
					variant.addModels(variant.partialState().with(DirectionalBlock.FACING, Direction.DOWN), new ConfiguredModel(modelFile, 90, 0, false));
				}

				for (var element : DataGenDefinitions.cubeBlocks) {
					cubeAll(element.get());
				}
			}
		});
		generator.addProvider(event.includeClient(), new ItemModelProvider(output, SurgeofKhroma.MODID, fileHelper) {
			@Override
			protected void registerModels() {
				for (var element : DataGenDefinitions.simpleItemBlocks)
					simpleBlockItem(element.get().getBlock());

				for (var element : DataGenDefinitions.simpleItems)
					basicItem(element.get());

				for (var element : DataGenDefinitions.handheldItems)
					handheldItem(element.get());
			}
		});

		generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, registrySet(), Set.of(SurgeofKhroma.MODID)));

		var blockTags = new TagsDataGen.BlockTag(output, lookupProvider, fileHelper);
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), new TagsDataGen.ItemTag(output, lookupProvider, blockTags.contentsGetter(), fileHelper));
		generator.addProvider(event.includeServer(), new TagsDataGen.DamageTypeTag(output, lookupProvider, fileHelper));

		generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(BLootProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

		generator.addProvider(event.includeServer(), new RecipeDaraGen(output, lookupProvider));

		generator.addProvider(event.includeServer(), new CuriosDataProvider(SurgeofKhroma.MODID, output, fileHelper, lookupProvider) {
			@Override
			public void generate(Provider registries, ExistingFileHelper fileHelper) {
				createSlot("eyes").order(50).icon(SurgeofKhroma.resource("slot/empty_eyes_slot")).addValidator(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, "tag"));
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
