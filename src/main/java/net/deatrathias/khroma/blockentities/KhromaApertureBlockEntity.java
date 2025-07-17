package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.KhromaApertureBlock;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumerBlock;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaApertureBlockEntity extends BaseKhromaUserBlockEntity implements MenuProvider, IKhromaProviderBlock, IKhromaConsumerBlock {

	private static final Component CONTAINER_TITLE = Component.translatable("container." + SurgeofKhroma.MODID + ".khroma_aperture");

	private float limit;

	private KhromaProviderImpl provider = new KhromaProviderImpl(true, () -> provides(), true);;

	private KhromaConsumerImpl consumer = new KhromaConsumerImpl(true, (throughput, simulate) -> consumes(throughput, simulate), true);

	protected final DataSlot limitData = new DataSlot() {

		@Override
		public void set(int value) {
			KhromaApertureBlockEntity.this.limit = (float) value / Integer.MAX_VALUE;
		}

		@Override
		public int get() {
			return Mth.floor(KhromaApertureBlockEntity.this.limit * Integer.MAX_VALUE);
		}
	};

	public KhromaApertureBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_APERTURE.get(), pos, blockState);
		limit = 1;
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		Direction facing = getBlockState().getValue(KhromaApertureBlock.FACING);
		if (facing == direction)
			return ConnectionType.PROVIDER;
		if (facing == direction.getOpposite())
			return ConnectionType.CONSUMER;
		return ConnectionType.NONE;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider registries) {
		super.loadAdditional(tag, registries);
		limit = tag.getFloat("limit").orElse(1f);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putFloat("limit", limit);
	}

	public float consumes(KhromaThroughput throughput, boolean simulate) {
		KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, getBlockState().getValue(KhromaApertureBlock.FACING)));

		if (networkFromProvider == null)
			return 0;

		if (!networkFromProvider.isRequestCalculatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromProvider);
			return -1;
		}

		return level.hasNeighborSignal(getBlockPos()) ? 0 : limit * networkFromProvider.getRequest();
	}

	public KhromaThroughput provides() {
		if (limit == 0 || level.hasNeighborSignal(getBlockPos()))
			return KhromaThroughput.empty;

		KhromaNetwork networkFromConsumer = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, getBlockState().getValue(KhromaApertureBlock.FACING).getOpposite()));
		if (networkFromConsumer == null)
			return KhromaThroughput.empty;

		if (!networkFromConsumer.isUpdatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromConsumer);
			return null;
		}

		KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, getBlockState().getValue(KhromaApertureBlock.FACING)));

		float request = networkFromProvider.getRequest(); // lastRequest;
		return new KhromaThroughput(networkFromConsumer.getKhroma(), networkFromConsumer.getKhromaRatio() * request * limit);
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new KhromaApertureMenu(containerId, playerInventory, ContainerLevelAccess.create(level, worldPosition), limitData);
	}

	@Override
	public Component getDisplayName() {
		return CONTAINER_TITLE;
	}

	public float getLimit() {
		return limit;
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaApertureBlock.FACING);
		if (face == facing.getOpposite()) {
			return consumer;
		}
		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaApertureBlock.FACING);
		if (face == facing) {
			return provider;
		}
		return KhromaProviderImpl.disabled;
	}
}
