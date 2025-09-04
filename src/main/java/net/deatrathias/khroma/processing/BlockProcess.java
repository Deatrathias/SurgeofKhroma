package net.deatrathias.khroma.processing;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class BlockProcess extends BaseProcess {

	public BlockProcess(ProcessType<?> type) {
		super(type);
	}

	public abstract boolean processBlock(float potency, Level level, List<BlockPos> pos);
}
