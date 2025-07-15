package net.deatrathias.khroma.blockentities;

import java.util.HashMap;
import java.util.Map;

import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKhromaUserBlockEntity extends BlockEntity {

	public enum ConnectionType {
		NONE, PROVIDER, CONSUMER
	}

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

	public abstract ConnectionType khromaConnection(Direction direction);

	public void dirtyNetwork() {

		for (Direction direction : Direction.values()) {
			dirtyNetwork(direction);
		}
	}

	public void dirtyNetwork(Direction direction) {
		ConnectionType connection = khromaConnection(direction);
		KhromaNetwork linkedNetwork = networkPerSide.get(direction);
		if (linkedNetwork != null)
			linkedNetwork.markDirty();
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(getBlockPos(), direction));
		if (network != null && linkedNetwork != network) {
			network.markDirty();
			if (connection != ConnectionType.NONE)
				networkPerSide.put(direction, network);
		}
		if (connection == ConnectionType.PROVIDER && linkedNetwork == null) {
			networkPerSide.put(direction, KhromaNetwork.create(level, new BlockDirection(getBlockPos(), direction)));
		}
	}
}
