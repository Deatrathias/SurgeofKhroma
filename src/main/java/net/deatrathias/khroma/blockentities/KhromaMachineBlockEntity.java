package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.KhromaMachineBlock;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaMachineBlockEntity extends BaseKhromaConsumerBlockEntity {

	private KhromaThroughput consumed;

	public KhromaMachineBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_MACHINE.get(), pos, blockState);
	}

	@Override
	public float consumes(KhromaThroughput throughput, boolean simulate) {
		return 1f;
	}

	@Override
	protected void tick() {
		consumed = requestOnSide(getBlockState().getValue(KhromaMachineBlock.FACING));
	}

	public void onUse() {
		SurgeofKhroma.LOGGER.debug("consumed: " + consumed.toString());
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		return getBlockState().getValue(KhromaMachineBlock.FACING) == direction ? ConnectionType.CONSUMER : ConnectionType.NONE;
	}

}
