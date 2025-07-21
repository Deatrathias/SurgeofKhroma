package net.deatrathias.khroma.datagen;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.items.renderer.SpannerColorTint;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaProperty;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition.Operation;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
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

	public ModelDataGen(PackOutput output) {
		super(output, SurgeofKhroma.MODID);
	}

	protected Stream<? extends Holder<Block>> getKnownBlocksa() {
		List<String> ignored = List.of("khroma_line");
		return super.getKnownBlocks().filter(holder -> !ignored.contains(holder.getKey().location().getPath()));
	}

	protected Stream<? extends Holder<Item>> getKnownItemsa() {
		List<String> ignored = List.of("khroma_line");
		return super.getKnownItems().filter(holder -> !ignored.contains(holder.getKey().location().getPath()));
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		for (var element : DataGenDefinitions.cubeBlocks) {
			blockModels.createTrivialCube(element.get());
		}

		for (var element : DataGenDefinitions.simpleBlocks) {
			blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(element.get(), BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element.get()))));
		}

		for (var element : DataGenDefinitions.horDirectionBlocks) {
			blockModels.blockStateOutput.accept(
					MultiVariantGenerator.dispatch(element.get(), BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element.get())))
							.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		}

		for (var element : DataGenDefinitions.fullDirectionBlocks) {
			blockModels.blockStateOutput.accept(
					MultiVariantGenerator.dispatch(element.get(), BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(element.get()))).with(BlockModelGenerators.ROTATION_FACING));
		}

		for (var element : DataGenDefinitions.simpleItems)
			itemModels.generateFlatItem(element.get(), ModelTemplates.FLAT_ITEM);

		for (var element : DataGenDefinitions.handheldItems)
			itemModels.generateFlatItem(element.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

		registerKhromaLine(blockModels, itemModels);
		registerDissipator(blockModels, itemModels);

		var spannerBase = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_base"),
				new TextureMapping().put(TextureSlot.LAYER0, SurgeofKhroma.resource("item/spanner_base")), itemModels.modelOutput);
		var spannerMiddle = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_middle"),
				new TextureMapping().put(TextureSlot.LAYER0, SurgeofKhroma.resource("item/spanner_middle")), itemModels.modelOutput);
		var spannerTop = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_top"),
				new TextureMapping().put(TextureSlot.LAYER0, SurgeofKhroma.resource("item/spanner_top")), itemModels.modelOutput);
		var spannerBottom = ModelTemplates.FLAT_HANDHELD_ITEM.create(SurgeofKhroma.resource("item/khroma_spanner_bottom"),
				new TextureMapping().put(TextureSlot.LAYER0, SurgeofKhroma.resource("item/spanner_bottom")), itemModels.modelOutput);

		itemModels.itemModelOutput.accept(RegistryReference.ITEM_KHROMETAL_SPANNER.get(), new CompositeModel.Unbaked(List.of(
				new BlockModelWrapper.Unbaked(spannerBase, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.BASE))),
				new BlockModelWrapper.Unbaked(spannerMiddle, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.MIDDLE))),
				new BlockModelWrapper.Unbaked(spannerTop, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.TOP))),
				new BlockModelWrapper.Unbaked(spannerBottom, List.of(new SpannerColorTint(SpannerItem.SpannerColorLocation.BOTTOM))))));
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

		var multipart = MultiPartGenerator.multiPart(RegistryReference.BLOCK_KHROMA_LINE.get())
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

		itemModels.itemModelOutput.accept(RegistryReference.ITEM_BLOCK_KHROMA_LINE.get(), new BlockModelWrapper.Unbaked(SurgeofKhroma.resource("item/khroma_line"), Collections.emptyList()));
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

		var multipart = MultiPartGenerator.multiPart(RegistryReference.BLOCK_KHROMA_DISSIPATOR.get());

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

	private MultiVariant plain(String path) {
		return BlockModelGenerators.plainVariant(SurgeofKhroma.resource(path));
	}
}
