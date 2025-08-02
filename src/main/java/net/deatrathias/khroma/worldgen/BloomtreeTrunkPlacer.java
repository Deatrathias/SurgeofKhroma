package net.deatrathias.khroma.worldgen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class BloomtreeTrunkPlacer extends GiantTrunkPlacer {
	public static final MapCodec<BloomtreeTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> trunkPlacerParts(instance)
					.and(instance.group(Codec.floatRange(0f, 1f).fieldOf("branchChance").forGetter(placer -> placer.branchChance),
							IntProvider.codec(0, 8).fieldOf("maxBranches").forGetter(placer -> placer.maxBranches),
							IntProvider.codec(1, 20).fieldOf("branchLength").forGetter(placer -> placer.branchLength)))
					.apply(instance, BloomtreeTrunkPlacer::new));

	private final float branchChance;

	private final IntProvider maxBranches;

	private final IntProvider branchLength;

	private static final Direction[] HORIZONTAL = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

	public BloomtreeTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, float branchChance, IntProvider maxBranches, IntProvider branchLength) {
		super(baseHeight, heightRandA, heightRandB);
		this.branchChance = branchChance;
		this.maxBranches = maxBranches;
		this.branchLength = branchLength;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return RegistryReference.TRUNK_PLACER_BLOOMTREE.get();
	}

	@Override
	public List<FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos,
			TreeConfiguration config) {
		List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
		list.addAll(super.placeTrunk(level, blockSetter, random, freeTreeHeight, pos, config));

		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(-1, 0, 0), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(-1, 0, 1), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(0, 0, -1), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(1, 0, -1), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(2, 0, 0), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(2, 0, 1), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(0, 0, 2), config);
		placeRoot(level, blockSetter, random, freeTreeHeight, pos.offset(1, 0, 2), config);

		Set<Direction> remainingDirs = new HashSet<Direction>(Arrays.asList(HORIZONTAL));
		int branchCount = 0;
		int maxBranchCount = maxBranches.sample(random);

		for (int i = freeTreeHeight - 4; i > 0; i--) {
			if (random.nextFloat() > branchChance)
				continue;
			int height = i;
			if (remainingDirs.isEmpty())
				remainingDirs.addAll(Arrays.asList(HORIZONTAL));
			Direction direction = (Direction) remainingDirs.toArray()[random.nextIntBetweenInclusive(0, remainingDirs.size() - 1)];
			remainingDirs.remove(direction);

			Function<BlockState, BlockState> stateSetter = state -> state.setValue(BlockStateProperties.AXIS, direction.getAxis());
			branchCount++;
			int x = direction == Direction.WEST ? 0 : 1;
			int z = direction == Direction.NORTH ? 0 : 1;
			BlockPos branchPos = pos.offset(x, height, z).relative(direction);
			if (random.nextBoolean()) {
				if (direction.getAxis() == Axis.X)
					branchPos = branchPos.offset(0, 0, -1);
				else
					branchPos = branchPos.offset(-1, 0, 0);
			}
			int length = branchLength.sample(random);

			for (int l = 0; l < length; l++) {
				placeLog(level, blockSetter, random, branchPos, config, stateSetter);
				branchPos = branchPos.relative(direction);
				if (l < length - 1 || random.nextFloat() > 0.5) {
					placeLog(level, blockSetter, random, branchPos, config);
					branchPos = branchPos.above();
				}
			}
			placeLog(level, blockSetter, random, branchPos, config);
			list.add(new FoliagePlacer.FoliageAttachment(branchPos.above(2), -2, false));

			if (branchCount >= maxBranchCount)
				break;
		}

		return list;
	}

	private void placeRoot(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos,
			TreeConfiguration config) {
		if (random.nextFloat() > 0.25f)
			placeLog(level, blockSetter, random, pos, config);
	}
}
