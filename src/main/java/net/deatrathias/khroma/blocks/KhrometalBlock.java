package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class KhrometalBlock extends Block {

	public KhrometalBlock(Properties properties) {
		super(properties);
	}

	public int maxAmplification() {
		return 5;
	}

	protected int getAmplification(Level level, BlockPos pos) {
		int amplify = 1;
		for (int i = 1; i < maxAmplification(); i++) {
			if (level.getBlockState(pos.below(i)).is(this))
				amplify++;
			else
				break;
		}
		return amplify;
	}
}
