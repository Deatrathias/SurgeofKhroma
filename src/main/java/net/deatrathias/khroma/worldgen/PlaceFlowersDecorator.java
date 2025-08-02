package net.deatrathias.khroma.worldgen;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class PlaceFlowersDecorator extends TreeDecorator {
	public static final MapCodec<PlaceFlowersDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance_ -> instance_.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(20).forGetter(decorator -> decorator.tries),
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").orElse(2).forGetter(decorator -> decorator.radius),
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("height").orElse(1).forGetter(decorator -> decorator.height))
					.apply(instance_, PlaceFlowersDecorator::new));

	private final int tries;
	private final int radius;
	private final int height;

	public PlaceFlowersDecorator(int tries, int radius, int height) {
		this.tries = tries;
		this.radius = radius;
		this.height = height;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return RegistryReference.DECORATOR_PLACE_FLOWERS.get();
	}

	@Override
	public void place(TreeDecorator.Context context) {
		if (!(context.level() instanceof ServerLevel level))
			return;

		List<BlockPos> list = TreeFeature.getLowestTrunkOrRootOfTree(context);
		if (!list.isEmpty()) {
			BlockPos first = list.getFirst();
			int i = first.getY();
			int j = first.getX();
			int k = first.getX();
			int l = first.getZ();
			int i1 = first.getZ();

			for (BlockPos pos : list) {
				if (pos.getY() == i) {
					j = Math.min(j, pos.getX());
					k = Math.max(k, pos.getX());
					l = Math.min(l, pos.getZ());
					i1 = Math.max(i1, pos.getZ());
				}
			}

			RandomSource random = context.random();
			BoundingBox boundingbox = new BoundingBox(j, i, l, k, i, i1).inflatedBy(this.radius, this.height, this.radius);
			BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

			for (int j1 = 0; j1 < this.tries; j1++) {
				mutablePos.set(
						random.nextIntBetweenInclusive(boundingbox.minX(), boundingbox.maxX()),
						random.nextIntBetweenInclusive(boundingbox.minY(), boundingbox.maxY()),
						random.nextIntBetweenInclusive(boundingbox.minZ(), boundingbox.maxZ()));
				var features = level.getBiome(mutablePos).value().getGenerationSettings().getFlowerFeatures();
				if (features.isEmpty())
					continue;

				int randId = random.nextInt(features.size());
				var holder = ((RandomPatchConfiguration) features.get(randId).config()).feature();

				holder.value().place(level, level.getChunkSource().getGenerator(), random, mutablePos);
			}
		}
	}

}
