package net.deatrathias.khroma.blocks;

import net.deatrathias.khroma.RegistryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhrometalRedBlock extends KhrometalBlock {

	public KhrometalRedBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity) {
			entity.hurt(level.damageSources().source(RegistryReference.DAMAGE_RED_KHROMETAL_BLOCK), 1f + getAmplification(level, pos));
		}
	}
}
