package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.Config;
import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NodeCollectorBlockEntity extends BaseKhromaUserBlockEntity implements IKhromaProviderBlock {

	private KhromaProviderImpl provider = new KhromaProviderImpl(true, () -> provides(), false);

	public NodeCollectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_NODE_COLLECTOR.get(), pos, blockState);
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		return direction == Direction.DOWN ? ConnectionType.PROVIDER : ConnectionType.NONE;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face) {
		if (khromaConnection(face) == ConnectionType.PROVIDER)
			return provider;
		return KhromaProviderImpl.disabled;
	}

	public KhromaThroughput provides() {
		return KhromaThroughput.empty;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, NodeCollectorBlockEntity blockEntity) {
		KhromaBiomeData data = level.getChunk(blockEntity.getBlockPos()).getData(RegistryReference.KHROMA_BIOME_DATA);
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, Direction.DOWN));
		if (network != null)
			network.provide(new KhromaThroughput(data.getNode().getKhroma(), Config.KHROMA_RATE_PER_LEVEL.get().get(data.getNode().getLevel() - 1).floatValue()));
	}
}
