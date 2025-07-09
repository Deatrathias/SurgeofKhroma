package net.deatrathias.khroma.khroma;

import java.util.Arrays;

import org.jetbrains.annotations.UnknownNullability;

import net.deatrathias.khroma.Config;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class KhromaBiomeData implements INBTSerializable<CompoundTag> {
	private boolean generated;

	private KhromaNode node;

	private static final int red = 0;
	private static final int green = 1;
	private static final int blue = 2;
	private static final int white = 3;
	private static final int black = 4;

	public KhromaBiomeData() {
		generated = false;
		node = null;
	}

	public boolean isGenerated() {
		return generated;
	}

	public KhromaNode getNode() {
		return node;
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(Provider provider) {
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("generated", generated);
		if (node != null)
			nbt.put("node", node.serializeNBT(provider));
		return nbt;
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag nbt) {
		generated = nbt.getBoolean("generated");
		if (nbt.contains("node"))
			node = new KhromaNode(provider, nbt.getCompound("node"));
		else
			node = null;
	}

	private long getSeed(long a, long b, long c) {
		return (((((1586624L ^ a) * 1000347L) ^ b) * 203687L) ^ c) * 463257L + 98561L;
	}

	public void generateNode(LevelAccessor level, ChunkAccess chunk) {
		int chunksPerNode = Config.CHUNKS_PER_NODE.getAsInt();
		ChunkPos chunkPos = chunk.getPos();
		int groupx = Math.abs(chunkPos.x % chunksPerNode);
		int groupz = Math.abs(chunkPos.z % chunksPerNode);
		long seed = getSeed(Math.floorDiv(chunkPos.x, chunksPerNode), Math.floorDiv(chunkPos.z, chunksPerNode), ((WorldGenLevel) level).getSeed());
		RandomSource random = RandomSource.create(seed);
		int x = random.nextIntBetweenInclusive(0, chunksPerNode - 1);
		int z = random.nextIntBetweenInclusive(0, chunksPerNode - 1);
		if (groupx != x || groupz != z) {
			generated = true;
			node = null;
			return;
		}

		int height = chunk.getHeight(Types.OCEAN_FLOOR, 8, 8);

		BlockPos nodePos = new BlockPos(chunkPos.getMiddleBlockX(), height + 4, chunkPos.getMiddleBlockZ());
		Khroma color = determineColor(chunk.getNoiseBiome(nodePos.getX() / 4, nodePos.getY() / 4, nodePos.getZ() / 4), nodePos, random);
		if (color != Khroma.empty())
			node = new KhromaNode(nodePos, color, 1);
		SurgeofKhroma.LOGGER.debug("seed " + seed + " chunk x " + chunkPos.x + " z " + chunkPos.z);
		generated = true;
	}

	private static Khroma determineColor(Holder<Biome> biomeHolder, BlockPos nodePos, RandomSource random) {
		float[] colorValues = new float[5];
		Arrays.fill(colorValues, 0);
		for (int i = 0; i < colorValues.length; i++) {
			colorValues[i] += random.nextFloat() * 0.2f;
		}
		float temperature = biomeHolder.value().getBaseTemperature();
		colorValues[red] += Math.max(0, temperature);
		colorValues[white] += Math.max(0, -temperature);

		if (biomeHolder.is(BiomeTags.IS_FOREST))
			colorValues[green] += 0.5f;

		if (biomeHolder.is(BiomeTags.IS_JUNGLE))
			colorValues[green] += 1f;

		if (biomeHolder.is(BiomeTags.INCREASED_FIRE_BURNOUT))
			colorValues[blue] += 0.4f;

		if (biomeHolder.is(BiomeTags.IS_RIVER))
			colorValues[blue] += 0.5f;

		if (biomeHolder.is(BiomeTags.IS_BEACH))
			colorValues[blue] += 0.5f;

		if (biomeHolder.is(BiomeTags.IS_OCEAN))
			colorValues[blue] += 1f;

		if (biomeHolder.is(BiomeTags.IS_DEEP_OCEAN))
			colorValues[blue] += 1f;

		if (biomeHolder.is(BiomeTags.IS_HILL))
			colorValues[white] += 0.4f;

		if (biomeHolder.is(BiomeTags.IS_MOUNTAIN))
			colorValues[white] += 1f;

		colorValues[black] += random.nextFloat() * 0.2f;

		SurgeofKhroma.LOGGER.debug(colorValues[0] + " " + colorValues[1] + " " + colorValues[2] + " " + colorValues[3] + " " + colorValues[4]);
		int color1 = findRandomValue(colorValues, random);
		if (color1 == -1)
			return Khroma.empty();

		int color2 = findRandomValue(colorValues, random);

		int color = 1 << color1;
		if (color2 != -1)
			color = color | (1 << color2);

		SurgeofKhroma.LOGGER.debug("node " + Khroma.fromInt(color) + " at " + nodePos.toString() + " biome " + biomeHolder.getRegisteredName());
		return Khroma.fromInt(color);
	}

	private static int findRandomValue(float[] colorValues, RandomSource random) {
		float total = 0;
		for (int i = 0; i < colorValues.length; i++)
			total += colorValues[i];

		if (total == 0)
			return -1;

		float selected = random.nextFloat() * total;
		for (int i = 0; i < colorValues.length; i++) {
			selected -= colorValues[i];
			if (selected < 0) {
				colorValues[i] = 0;
				return i;
			}
		}

		return -1;
	}
}
