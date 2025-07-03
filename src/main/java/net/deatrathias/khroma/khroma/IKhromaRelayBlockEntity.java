package net.deatrathias.khroma.khroma;

import net.minecraft.core.Direction;

public interface IKhromaRelayBlockEntity {
	float consumes(KhromaThroughput throughput, boolean simulate, Direction side);

	KhromaThroughput provides(Direction side);
}
