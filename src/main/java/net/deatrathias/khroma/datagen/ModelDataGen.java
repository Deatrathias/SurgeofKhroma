package net.deatrathias.khroma.datagen;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.PillarBlock;
import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.items.renderer.SpannerColorTint;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaProperty;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.ItemReference;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.BlockModelGenerators.PlantType;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition.Operation;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModelDataGen extends ModelProvider {
	public static final Map<Direction, VariantMutator> MULTIFACE_GENERATOR_NO_LOCK = ImmutableMap.of(
			Direction.NORTH,
			BlockModelGenerators.NOP,
			Direction.EAST,
			BlockModelGenerators.Y_ROT_90,
			Direction.SOUTH,
			BlockModelGenerators.Y_ROT_180,
			Direction.WEST,
			BlockModelGenerators.Y_ROT_270,
			Direction.UP,
			BlockModelGenerators.X_ROT_270,
			Direction.DOWN,
			BlockModelGenerators.X_ROT_90);

	public static final ModelTemplate PILLAR_MIDDLE = ModelTemplates.create(SurgeofKhroma.MODID + ":pillar_middle", "_middle", TextureSlot.TEXTURE);
	public static final ModelTemplate PILLAR_TOP = ModelTemplates.create(SurgeofKhroma.MODID + ":pillar_top", "_top", TextureSlot.TEXTURE);
	public static final ModelTemplate PILLAR_BOTTOM = ModelTemplates.create(SurgeofKhroma.MODID + ":pillar_bottom", "_bottom", TextureSlot.TEXTURE);
	public static final ModelTemplate PILLAR_TOP_BOTTOM = ModelTemplates.create(SurgeofKhroma.MODID + ":pillar_top_bottom", "_top_bottom", TextureSlot.TEXTURE);

	public ModelDataGen(PackOutput output) {
		super(output, SurgeofKhroma.MODID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		for (var element : DataGenDefinitions.cubeBlocks) {
			blockModels.createTrivialCube(element);
		}

		for (var tree : BlockReference.IMBUED_TREES) {
			blockModels.woodProvider(tree.get(TreeBlock.LOG)).logWithHorizontal(tree.get(TreeBlock.LOG)).wood(tree.get(TreeBlock.WOOD));
			blockModels.woodProvider(tree.get(TreeBlock.STRIPPED_LOG)).logWithHorizontal(tree.get(TreeBlock.STRIPPED_LOG)).wood(tree.get(TreeBlock.STRIPPED_WOOD));
			blockModels.createTrivialBlock(tree.get(TreeBlock.LEAVES), TexturedModel.LEAVES);
			blockModels.createPlantWithDefaultItem(tree.get(TreeBlock.SAPLING), tree.get(TreeBlock.POTTED_SAPLING), PlantType.NOT_TINTED);
			tree.ifPresent(TreeBlock.HANGING_SIGN, block -> blockModels.createHangingSign(tree.get(TreeBlock.STRIPPED_LOG), block, tree.get(TreeBlock.WALL_HANGING_SIGN)));
			blockModels.family(tree.get(TreeBlock.PLANKS)).generateFor(tree.getFamily());
			if (tree.getBoatItem() != null)
				itemModels.generateFlatItem(tree.getBoatItem().get(), ModelTemplates.FLAT_ITEM);
			if (tree.getChestBoatItem() != null)
				itemModels.generateFlatItem(tree.getChestBoatItem().get(), ModelTemplates.FLAT_ITEM);
			tree.ifPresent(TreeBlock.PILLAR, block -> createPillar(block, TextureMapping.defaultTexture(tree.get(TreeBlock.PLANKS)), blockModels));
		}

		for (var element : DataGenDefinitions.simpleBlocks) {
			blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(element, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element))));
		}

		for (var element : DataGenDefinitions.horDirectionBlocks) {
			blockModels.blockStateOutput.accept(
					MultiVariantGenerator.dispatch(element, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element)))
							.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		}

		for (var element : DataGenDefinitions.fullDirectionBlocks) {
			blockModels.blockStateOutput.accept(
					MultiVariantGenerator.dispatch(element, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element))).with(BlockModelGenerators.ROTATION_FACING));
		}

		for (var element : DataGenDefinitions.simpleItems)
			itemModels.generateFlatItem(element, ModelTemplates.FLAT_ITEM);

		for (var element : DataGenDefinitions.handheldItems)
			itemModels.generateFlatItem(element, ModelTemplates.FLAT_HANDHELD_ITEM);

		registerKhromaLine(blockModels, itemModels);
		registerDissipator(blockModels, itemModels);

		var spannerBase = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_base"), TextureMapping.layer0(SurgeofKhroma.resource("item/spanner_base")),
				itemModels.modelOutput);
		var spannerMiddle = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_middle"), TextureMapping.layer0(SurgeofKhroma.resource("item/spanner_middle")),
				itemModels.modelOutput);
		var spannerTop = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_top"), TextureMapping.layer0(SurgeofKhroma.resource("item/spanner_top")),
				itemModels.modelOutput);
		var spannerBottom = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_bottom"), TextureMapping.layer0(SurgeofKhroma.resource("item/spanner_bottom")),
				itemModels.modelOutput);

		itemModels.itemModelOutput.accept(ItemReference.KHROMETAL_SPANNER.get(), new CompositeModel.Unbaked(List.of(
				new BlockModelWrapper.Unbaked(spannerBase, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.BASE))),
				new BlockModelWrapper.Unbaked(spannerMiddle, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.MIDDLE))),
				new BlockModelWrapper.Unbaked(spannerTop, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.TOP))),
				new BlockModelWrapper.Unbaked(spannerBottom, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.BOTTOM))))));

		var warpCanister = ModelTemplates.FLAT_ITEM.create(SurgeofKhroma.resource("item/warp_canister"), TextureMapping.layer0(ItemReference.WARP_CANISTER.get()), itemModels.modelOutput);
		var warpCanisterActive = ModelTemplates.FLAT_ITEM.create(SurgeofKhroma.resource("item/warp_canister_active"), TextureMapping.layer0(SurgeofKhroma.resource("item/warp_canister_active")),
				itemModels.modelOutput);

		itemModels.itemModelOutput.accept(ItemReference.WARP_CANISTER.get(),
				new ConditionalItemModel.Unbaked(new HasComponent(RegistryReference.DATA_COMPONENT_CONTAINER_LINK_LOCATION.get(), false), new BlockModelWrapper.Unbaked(warpCanisterActive, List.of()),
						new BlockModelWrapper.Unbaked(warpCanister, List.of())));
	}

	private void registerKhromaLine(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		var notEmptyCondition = BlockModelGenerators.condition().negatedTerm(KhromaLineBlock.KHROMA, Khroma.KHROMA_EMPTY).build();
		var notSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaLineBlock.KHROMA, Khroma.KHROMA_SPECTRUM).build();
		var notLightSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaLineBlock.KHROMA, Khroma.KHROMA_LIGHT_SPECTRUM).build();
		var notDarkSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaLineBlock.KHROMA, Khroma.KHROMA_DARK_SPECTRUM).build();
		var notKhromegaCondition = BlockModelGenerators.condition().negatedTerm(KhromaLineBlock.KHROMA, Khroma.KHROMA_KHROMEGA).build();
		var spectrumCondition = BlockModelGenerators.condition().term(KhromaLineBlock.KHROMA, Khroma.KHROMA_SPECTRUM).build();
		var lightSpectrumCondition = BlockModelGenerators.condition().term(KhromaLineBlock.KHROMA, Khroma.KHROMA_LIGHT_SPECTRUM).build();
		var darkSpectrumCondition = BlockModelGenerators.condition().term(KhromaLineBlock.KHROMA, Khroma.KHROMA_DARK_SPECTRUM).build();
		var khromegaCondition = BlockModelGenerators.condition().term(KhromaLineBlock.KHROMA, Khroma.KHROMA_KHROMEGA).build();

		var multipart = MultiPartGenerator.multiPart(BlockReference.KHROMA_LINE.get())
				.with(BlockModelGenerators.plainVariant(SurgeofKhroma.resource("block/khroma_line_center")))
				.with(new CombinedCondition(Operation.AND, List.of(
						notEmptyCondition,
						notSpectrumCondition,
						notLightSpectrumCondition,
						notDarkSpectrumCondition,
						notKhromegaCondition)),
						plain("block/khroma_line_center_inside"))
				.with(spectrumCondition,
						plain("block/khroma_line_center_inside_spectrum"))
				.with(lightSpectrumCondition,
						plain("block/khroma_line_center_inside_spectrum_white"))
				.with(darkSpectrumCondition,
						plain("block/khroma_line_center_inside_spectrum_black"))
				.with(khromegaCondition,
						plain("block/khroma_line_center_inside_khromega"));

		for (Direction direction : Direction.values()) {
			var directionVariant = MULTIFACE_GENERATOR_NO_LOCK.get(direction);
			var directionCondition = BlockModelGenerators.condition().term(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(direction), true).build();
			multipart
					.with(directionCondition,
							plain("block/khroma_line_side").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							directionCondition,
							notEmptyCondition,
							notSpectrumCondition,
							notLightSpectrumCondition,
							notDarkSpectrumCondition,
							notKhromegaCondition)),
							plain("block/khroma_line_side_inside").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							directionCondition,
							spectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							directionCondition,
							lightSpectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum_white").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							directionCondition,
							darkSpectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum_black").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							directionCondition,
							khromegaCondition)),
							plain("block/khroma_line_side_inside_khromega").with(directionVariant));
		}

		blockModels.blockStateOutput.accept(multipart);

		itemModels.itemModelOutput.accept(BlockReference.KHROMA_LINE.asItem(), new BlockModelWrapper.Unbaked(SurgeofKhroma.resource("item/khroma_line"), Collections.emptyList()));
	}

	private void registerDissipator(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		var notEmptyCondition = BlockModelGenerators.condition().negatedTerm(KhromaProperty.KHROMA, Khroma.KHROMA_EMPTY).build();
		var notSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaProperty.KHROMA, Khroma.KHROMA_SPECTRUM).build();
		var notLightSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaProperty.KHROMA, Khroma.KHROMA_LIGHT_SPECTRUM).build();
		var notDarkSpectrumCondition = BlockModelGenerators.condition().negatedTerm(KhromaProperty.KHROMA, Khroma.KHROMA_DARK_SPECTRUM).build();
		var notKhromegaCondition = BlockModelGenerators.condition().negatedTerm(KhromaProperty.KHROMA, Khroma.KHROMA_KHROMEGA).build();
		var spectrumCondition = BlockModelGenerators.condition().term(KhromaProperty.KHROMA, Khroma.KHROMA_SPECTRUM).build();
		var lightSpectrumCondition = BlockModelGenerators.condition().term(KhromaProperty.KHROMA, Khroma.KHROMA_LIGHT_SPECTRUM).build();
		var darkSpectrumCondition = BlockModelGenerators.condition().term(KhromaProperty.KHROMA, Khroma.KHROMA_DARK_SPECTRUM).build();
		var khromegaCondition = BlockModelGenerators.condition().term(KhromaProperty.KHROMA, Khroma.KHROMA_KHROMEGA).build();

		var variants = BlockModelGenerators.plainVariant(SurgeofKhroma.resource("block/khroma_dissipator"));

		var multipart = MultiPartGenerator.multiPart(BlockReference.KHROMA_DISSIPATOR.get());

		for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
			var directionCondition = BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, direction).build();
			var reverseDirectionCondition = BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite()).build();
			var directionVariant = MULTIFACE_GENERATOR_NO_LOCK.get(direction);

			multipart.with(directionCondition, variants.with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							reverseDirectionCondition,
							notEmptyCondition,
							notSpectrumCondition,
							notLightSpectrumCondition,
							notDarkSpectrumCondition,
							notKhromegaCondition)),
							plain("block/khroma_line_side_inside").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							reverseDirectionCondition,
							spectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							reverseDirectionCondition,
							lightSpectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum_white").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							reverseDirectionCondition,
							darkSpectrumCondition)),
							plain("block/khroma_line_side_inside_spectrum_black").with(directionVariant))
					.with(new CombinedCondition(Operation.AND, List.of(
							reverseDirectionCondition,
							khromegaCondition)),
							plain("block/khroma_line_side_inside_khromega").with(directionVariant));
		}

		blockModels.blockStateOutput.accept(multipart);
	}

	private void createPillar(Block block, TextureMapping mapping, BlockModelGenerators blockModels) {
		ResourceLocation modelTopBottom = PILLAR_TOP_BOTTOM.create(block, mapping, blockModels.modelOutput);
		MultiVariant pillarMiddle = BlockModelGenerators.plainVariant(PILLAR_MIDDLE.create(block, mapping, blockModels.modelOutput));
		MultiVariant pillarTop = BlockModelGenerators.plainVariant(PILLAR_TOP.create(block, mapping, blockModels.modelOutput));
		MultiVariant pillarBottom = BlockModelGenerators.plainVariant(PILLAR_BOTTOM.create(block, mapping, blockModels.modelOutput));
		MultiVariant pillarTopBottom = BlockModelGenerators.plainVariant(modelTopBottom);

		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(PillarBlock.TOP, PillarBlock.BOTTOM)
						.select(false, false, pillarMiddle)
						.select(true, false, pillarTop)
						.select(false, true, pillarBottom)
						.select(true, true, pillarTopBottom)));
		blockModels.registerSimpleItemModel(block, modelTopBottom);

	}

	private MultiVariant plain(String path) {
		return BlockModelGenerators.plainVariant(SurgeofKhroma.resource(path));
	}
}
