package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blocks.KhromaProviderBlock;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaProviderBlockEntity extends BaseKhromaUserBlockEntity implements IKhromaProviderBlock {

	private Khroma khroma;

	private Khroma[] cycle = new Khroma[] { Khroma.get(true, true, false, false, false), Khroma.get(true, false, true, false, false), Khroma.get(true, false, false, true, false),
			Khroma.get(true, false, false, false, true), Khroma.get(false, true, true, false, false), Khroma.get(false, true, false, true, false), Khroma.get(false, true, false, false, true),
			Khroma.get(false, false, true, true, false), Khroma.get(false, false, true, false, true), Khroma.get(false, false, false, true, true) };

	private KhromaProviderImpl provider = new KhromaProviderImpl(true, () -> provides(), false);

	public KhromaProviderBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_PROVIDER.get(), pos, blockState);
		khroma = cycle[0];
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider registries) {
		super.loadAdditional(tag, registries);
		khroma = Khroma.fromInt(tag.getInt("khroma"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("khroma", khroma.asInt());
	}

	public void use(Player player) {
		int id = -1;
		for (int i = 0; i < cycle.length; i++) {
			if (cycle[i] == khroma) {
				id = i;
				break;
			}
		}
		id = (id + 1) % cycle.length;
		khroma = cycle[id];
		setChanged();
	}

	public KhromaThroughput provides() {
		return new KhromaThroughput(khroma, 60);
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		return getBlockState().getValue(KhromaProviderBlock.FACING) == direction ? ConnectionType.PROVIDER : ConnectionType.NONE;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face) {
		if (face == state.getValue(KhromaProviderBlock.FACING))
			return provider;

		return KhromaProviderImpl.disabled;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, KhromaProviderBlockEntity blockEntity) {
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, state.getValue(KhromaProviderBlock.FACING)));
		if (network != null)
			network.provide(new KhromaThroughput(blockEntity.khroma, 60));
	}
}
