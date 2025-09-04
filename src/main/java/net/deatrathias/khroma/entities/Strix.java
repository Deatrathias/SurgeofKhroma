package net.deatrathias.khroma.entities;

import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class Strix extends Monster {
	public static enum StrixPose {
		STANDING(0),
		FLYING(1),
		HOVERING(2);

		public static final IntFunction<StrixPose> BY_ID = ByIdMap.continuous(StrixPose::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, StrixPose> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, StrixPose::id);
		private final int id;

		private StrixPose(int id) {
			this.id = id;
		}

		public int id() {
			return id;
		}
	}

	public static final EntityDataAccessor<StrixPose> DATA_STRIX_POSE = SynchedEntityData.defineId(Strix.class, RegistryReference.SERIALIZER_STRIX_POSE.get());
	public final AnimationState standAnimationState = new AnimationState();
	public final AnimationState flyAnimationState = new AnimationState();
	public final AnimationState hoverAnimationState = new AnimationState();

	public Strix(EntityType<Strix> entityType, Level level) {
		super(entityType, level);
		this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
		this.setPathfindingMalus(PathType.WATER, -1.0F);
		this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
		this.xpReward = XP_REWARD_HUGE;
	}

	@Override
	protected void defineSynchedData(Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_STRIX_POSE, StrixPose.FLYING);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (DATA_STRIX_POSE.equals(key)) {
			switch (entityData.get(DATA_STRIX_POSE)) {
			case FLYING:
				flyAnimationState.start(tickCount);
				break;
			case HOVERING:
				hoverAnimationState.start(tickCount);
				break;
			case STANDING:
				standAnimationState.start(tickCount);
				break;
			default:
				break;

			}
		}

		super.onSyncedDataUpdated(key);
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
	}

	@Override
	public boolean onClimbable() {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 200.0)
				.add(Attributes.MOVEMENT_SPEED, 1.5f)
				.add(Attributes.FLYING_SPEED, 2f)
				.add(Attributes.ATTACK_KNOCKBACK, 1.5)
				.add(Attributes.ATTACK_DAMAGE, 10.0)
				.add(Attributes.FOLLOW_RANGE, 10.0);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		var navigation = new FlyingPathNavigation(this, level);
		navigation.setCanFloat(false);
		navigation.setCanOpenDoors(false);
		return navigation;
	}

	@Override
	public void tick() {
		super.tick();
	}
}
