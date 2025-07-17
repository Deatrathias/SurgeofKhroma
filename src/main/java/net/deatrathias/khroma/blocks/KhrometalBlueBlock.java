package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhrometalBlueBlock extends KhrometalBlock {

	public KhrometalBlueBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(MobEffects.SPEED, 10, getAmplification(level, pos)));
		}
	}
}
