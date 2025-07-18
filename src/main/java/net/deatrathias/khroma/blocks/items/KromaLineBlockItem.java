package net.deatrathias.khroma.blocks.items;

import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class KromaLineBlockItem extends BlockItem {

	public KromaLineBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		BlockPos currentPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		Level level = context.getLevel();
		BlockState currentState = level.getBlockState(currentPos);
		BlockState nearbyState = level.getBlockState(context.getClickedPos());

		BlockState lineState = null;
		BlockPos linePos = null;
		Direction lineSide = Direction.DOWN;

		if (currentState.is(getBlock())) {
			lineState = currentState;
			linePos = currentPos;
			lineSide = context.getClickedFace();
		} else if (nearbyState.is(getBlock())) {
			lineState = nearbyState;
			linePos = context.getClickedPos();
			lineSide = context.getClickedFace().getOpposite();
		}

		if (lineState != null && connectLine(level, lineState, linePos, lineSide, context.getPlayer()))
			return InteractionResult.SUCCESS_SERVER;
		;

		return super.place(context);
	}

	private boolean connectLine(Level level, BlockState state, BlockPos pos, Direction side, Player player) {
		if (!state.getValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(side)) && KhromaLineBlock.canSideConnect(level, state, pos, side)) {
			level.setBlockAndUpdate(pos, state.setValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(side), true));

			SoundType soundtype = state.getSoundType(level, pos, player);
			level.playSound(player, pos, this.getPlaceSound(state, level, pos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			return true;
		}

		return false;
	}
}
