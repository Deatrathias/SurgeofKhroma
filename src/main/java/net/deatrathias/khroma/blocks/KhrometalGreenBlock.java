package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhrometalGreenBlock extends KhrometalBlock {

	public KhrometalGreenBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.heal(0.01f * getAmplification(level, pos));
		}
	}
}
