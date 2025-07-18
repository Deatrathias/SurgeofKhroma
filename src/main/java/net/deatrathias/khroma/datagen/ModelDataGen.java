package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.stream.Stream;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelDataGen extends ModelProvider {

	public ModelDataGen(PackOutput output) {
		super(output, SurgeofKhroma.MODID);
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		List<String> ignored = List.of("khroma_line");
		return super.getKnownBlocks().filter(holder -> !ignored.contains(holder.getKey().location().getPath()));
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
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

		// for (var element : DataGenDefinitions.simpleItemBlocks)
		// itemModels. (element.get().getBlock());

		for (var element : DataGenDefinitions.simpleItems)
			itemModels.generateFlatItem(element.get(), ModelTemplates.FLAT_ITEM);

		for (var element : DataGenDefinitions.handheldItems)
			itemModels.generateFlatItem(element.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
	}

	private void registerKhromaLine(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

	}
}
