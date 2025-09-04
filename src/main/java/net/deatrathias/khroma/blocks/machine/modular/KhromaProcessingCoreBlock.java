package net.deatrathias.khroma.blocks.machine.modular;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.blockentities.KhromaProcessingCoreBlockEntity;
import net.deatrathias.khroma.blocks.IKhromaDevice;
import net.deatrathias.khroma.khroma.KhromaDeviceTier;
import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaProcessingCoreBlock extends BaseEntityBlock implements IKhromaDevice {

	public static final MapCodec<KhromaProcessingCoreBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(KhromaDeviceTier.CODEC.fieldOf("tier").forGetter(KhromaProcessingCoreBlock::getTier), propertiesCodec()).apply(instance, KhromaProcessingCoreBlock::new));

	private final KhromaDeviceTier tier;

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public KhromaProcessingCoreBlock(KhromaDeviceTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	@Override
	public KhromaDeviceTier getTier() {
		return tier;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaProcessingCoreBlockEntity(pos, state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState,
			RandomSource random) {
		level.getBlockEntity(pos, BlockReference.BE_KHROMA_PROCESSING_CORE.get()).ifPresent(be -> be.refreshNeighborChanges());
		return state;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, BlockReference.BE_KHROMA_PROCESSING_CORE.get(), KhromaProcessingCoreBlockEntity::serverTick);
	}
}
