package net.deatrathias.khroma.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;

public class FixedTintParticleLeavesBlock extends TintedParticleLeavesBlock {
	public static final MapCodec<FixedTintParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter(block -> block.leafParticleChance),
					Codec.INT.fieldOf("color").forGetter(block -> block.color),
					propertiesCodec())
					.apply(instance, FixedTintParticleLeavesBlock::new));

	private final int color;

	public FixedTintParticleLeavesBlock(float leafParticleChance, int color, Properties properties) {
		super(leafParticleChance, properties);
		this.color = color;
	}

	@Override
	protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource random) {
		ColorParticleOption colorparticleoption = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, color);
		ParticleUtils.spawnParticleBelow(level, blockPos, random, colorparticleoption);
	}

	@Override
	public MapCodec<? extends FixedTintParticleLeavesBlock> codec() {
		return CODEC;
	}
}
