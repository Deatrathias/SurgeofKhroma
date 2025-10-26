package net.deatrathias.khroma.blocks.machine.modular;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.blockentities.ItemOutputModuleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ItemOutputModuleBlock extends BaseEntityBlock {

	public static final MapCodec<ItemOutputModuleBlock> CODEC = simpleCodec(ItemOutputModuleBlock::new);
	private static final VoxelShape SHAPE = Block.cube(12);

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public ItemOutputModuleBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemOutputModuleBlockEntity(pos, state);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		var handler = level.getCapability(Capabilities.Item.BLOCK, pos, hitResult.getDirection());
		if (handler == null)
			return InteractionResult.PASS;
		
		ItemStack item = ItemStack.EMPTY;
		try (Transaction tx = Transaction.openRoot()) {
			var resource = handler.getResource(0);
			int transfered = handler.extract(resource, resource.getMaxStackSize(), tx);
			item = resource.toStack(transfered);
			tx.commit();
		}
		
		if (!item.isEmpty()) {
			if (!level.isClientSide()) {
				Vec3 center = pos.getCenter();
				ItemEntity entity = new ItemEntity(level, center.x, center.y, center.z, item);
				entity.setNoPickUpDelay();
				level.addFreshEntity(entity);
				entity.playerTouch(player);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
