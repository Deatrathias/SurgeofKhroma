package net.deatrathias.khroma.blocks.machine;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.blockentities.KhromaFabricatorBlockEntity;
import net.deatrathias.khroma.blocks.BaseKhromaUserEntityBlock;
import net.deatrathias.khroma.blocks.IKhromaDevice;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumingBlock;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaDeviceTier;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.util.EightDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaFabricatorBlock extends BaseKhromaUserEntityBlock implements IKhromaDevice, IKhromaConsumingBlock {

	public static final MapCodec<KhromaFabricatorBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(KhromaDeviceTier.CODEC.fieldOf("tier").forGetter(KhromaFabricatorBlock::getTier), propertiesCodec()).apply(instance, KhromaFabricatorBlock::new));

	private final KhromaDeviceTier tier;

	@Override
	protected MapCodec<KhromaFabricatorBlock> codec() {
		return CODEC;
	}

	public KhromaFabricatorBlock(KhromaDeviceTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaFabricatorBlockEntity(pos, state);
	}

	@Override
	public KhromaDeviceTier getTier() {
		return tier;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide() ? null : createTickerHelper(blockEntityType, BlockReference.BE_KHROMA_FABRICATOR.get(), KhromaFabricatorBlockEntity::serverTick);
	}

	@Override
	protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int flags, int recursionLeft) {
		if (!level.getBlockState(pos).is(this))
			return;

		MutableBlockPos mutPos = pos.mutable().move(Direction.DOWN);
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0)
					continue;

				mutPos.setX(pos.getX() + x).setZ(pos.getZ() + z);
				BlockState neighborState = level.getBlockState(mutPos);
				if (neighborState.is(BlockReference.ITEM_PEDESTAL)) {
					EightDirection direction = EightDirection.findDirection(-x, -z);
					level.setBlock(mutPos, neighborState.setValue(ItemPedestalBlock.FACING, direction), flags, recursionLeft);
				}
			}
		}
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		return direction == Direction.UP ? ConnectionType.CONSUMER : ConnectionType.NONE;
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		var be = level.getBlockEntity(pos, BlockReference.BE_KHROMA_FABRICATOR.get());
		if (be.isPresent())
			return be.get().getConsumer(level, pos, state, face, network);
		return KhromaConsumerImpl.disabled;
	}
}
