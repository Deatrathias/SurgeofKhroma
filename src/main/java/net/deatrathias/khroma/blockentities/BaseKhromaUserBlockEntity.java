package net.deatrathias.khroma.blockentities;

import java.util.HashMap;
import java.util.Map;

import net.deatrathias.khroma.blocks.BaseKhromaUserBlock;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKhromaUserBlockEntity extends BlockEntity {

	protected Map<Direction, KhromaNetwork> networkPerSide;

	public BaseKhromaUserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
		networkPerSide = new HashMap<Direction, KhromaNetwork>();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		dirtyNetwork();
	}

	public void dirtyNetwork() {

		for (Direction direction : Direction.values()) {
			dirtyNetwork(direction);
		}
	}

	public void dirtyNetwork(Direction direction) {
		if (level.isClientSide)
			return;
		if (getBlockState().getBlock() instanceof BaseKhromaUserBlock user) {
			IKhromaUsingBlock.ConnectionType connection = user.khromaConnection(getBlockState(), direction);
			KhromaNetwork linkedNetwork = networkPerSide.get(direction);
			if (linkedNetwork != null)
				linkedNetwork.markDirty();
			KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(getBlockPos(), direction));
			if (network != null && linkedNetwork != network) {
				network.markDirty();
				if (connection != IKhromaUsingBlock.ConnectionType.NONE)
					networkPerSide.put(direction, network);
			}
			if (connection == IKhromaUsingBlock.ConnectionType.PROVIDER && linkedNetwork == null) {
				networkPerSide.put(direction, KhromaNetwork.create(level, new BlockDirection(getBlockPos(), direction)));
			}
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		dirtyNetwork();
	}
}
