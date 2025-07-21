package net.deatrathias.khroma.blocks;

import java.util.Optional;
import java.util.function.Function;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumingBlock;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProperty;
import net.deatrathias.khroma.particles.KhromaParticleOption;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KhromaDissipatorBlock extends BaseKhromaUserBlock implements IKhromaConsumingBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final KhromaProperty KHROMA = KhromaProperty.KHROMA;

	private final Function<BlockState, VoxelShape> shapes;

	public static final MapCodec<KhromaDissipatorBlock> CODEC = simpleCodec(KhromaDissipatorBlock::new);

	@Override
	protected MapCodec<KhromaDissipatorBlock> codec() {
		return CODEC;
	}

	public KhromaDissipatorBlock(Properties properties) {
		super(properties);
		shapes = makeShapes();
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(KHROMA, Khroma.KHROMA_EMPTY));
	}

	private Function<BlockState, VoxelShape> makeShapes() {
		var map = Shapes.rotateHorizontal(Shapes.or(Block.box(5, 5, 15, 11, 11, 16), Block.box(6, 6, 12, 10, 10, 15), Block.box(2, 3, 3, 14, 13, 12)));
		return getShapeForEachState(state -> map.get(state.getValue(FACING)));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.apply(state);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (state.is(this) && oldState.is(this) && oldState.setValue(KHROMA, state.getValue(KHROMA)) != state)
			super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING).add(KHROMA);
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		return state.getValue(FACING).getOpposite() == face ? new KhromaConsumerImpl() : KhromaConsumerImpl.disabled;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS_SERVER;
		else {
			var network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, state.getValue(FACING).getOpposite()));
			float rate = 0;
			if (network != null)
				rate = Math.round(network.getKhromaRatio() * 100f) / 100f;
			player.displayClientMessage(Component.translatable("block.surgeofkhroma.khroma_dissipator.message", Float.toString(rate)), true);
			return InteractionResult.SUCCESS_SERVER;
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		Khroma khroma = Optional.ofNullable(KhromaNetwork.findNetwork(level, new BlockDirection(pos, state.getValue(FACING).getOpposite()))).map(network -> network.getKhroma())
				.orElse(Khroma.KHROMA_EMPTY);
		if (khroma != state.getValue(KHROMA))
			level.setBlock(pos, state.setValue(KHROMA, khroma), UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Khroma khroma = state.getValue(KHROMA);
		Direction facing = state.getValue(FACING);
		if (khroma != Khroma.KHROMA_EMPTY) {
			Vector3f spawn = new Vector3f(randomBetween(-0.375f, 0.375f, random), randomBetween(-0.25f, 0.3125f, random), 0);
			spawn = facing.getRotation().transform(spawn);
			level.addParticle(new KhromaParticleOption(khroma), pos.getX() + spawn.x + 0.5, pos.getY() + spawn.y + 0.5, pos.getZ() + spawn.z + 0.5, 0, randomBetween(0.05f, 0.1f, random), 0);
		}
	}

	private float randomBetween(float min, float max, RandomSource random) {
		return random.nextFloat() * (max - min) + min;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		if (direction == state.getValue(FACING).getOpposite())
			return ConnectionType.CONSUMER;
		return ConnectionType.NONE;
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
