package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhrometalWhiteBlock extends KhrometalBlock {

	public KhrometalWhiteBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, getAmplification(level, pos)));
		}
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
	}
}
