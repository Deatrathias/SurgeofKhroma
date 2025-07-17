package net.deatrathias.khroma.blocks;

import org.joml.Vector3f;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.network.ServerboundWalkOnBlackKhrometalPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class KhrometalBlackBlock extends KhrometalBlock {

	public KhrometalBlackBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		// We don't have the delta movement on the server player so we only call it for
		// other entities and we send a message instead from the client
		if (entity.isSteppingCarefully() || (entity instanceof LivingEntity living && living.hasEffect(RegistryReference.TELEPORT_SICKNESS)))
			return;
		if (!level.isClientSide && !(entity instanceof Player))
			doTeleport(level, pos, entity, getDirectionFromMovement(entity.getDeltaMovement()));
		else if (level.isClientSide && entity instanceof Player player) {
			handleClientStep(player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void handleClientStep(Player player) {
		if (!player.isLocalPlayer())
			return;
		Direction direction = getDirectionFromMovement(player.getDeltaMovement());
		if (direction != null)
			Minecraft.getInstance().getConnection().send(new ServerboundWalkOnBlackKhrometalPacket(direction));
	}

	public void doTeleport(Level level, BlockPos pos, Entity entity, Direction direction) {
		if (direction == null)
			return;
		if (entity instanceof LivingEntity living) {
			Vec2 blockVec = new Vec2(pos.getX() + 0.5f, pos.getZ() + 0.5f);
			Vec2 entityVec = new Vec2((float) entity.getX(), (float) entity.getZ());
			if (blockVec.distanceToSqr(entityVec) < 0.64) {
				Vector3f teleport = direction.step().mul(2 + getAmplification(level, pos));
				if (living.isFree(teleport.x, teleport.y, teleport.z)) {
					level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS);
					living.teleportRelative(teleport.x, teleport.y, teleport.z);
					living.addEffect(new MobEffectInstance(RegistryReference.TELEPORT_SICKNESS, 60));
				}
			}
		}
	}

	@Override
	public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
		if (Math.abs(entity.getDeltaMovement().y) > 0.1)
			entity.setDeltaMovement(0, 0, 0);
		else
			super.updateEntityMovementAfterFallOn(level, entity);
	}

	private static Direction getDirectionFromMovement(Vec3 deltaMovement) {
		if (deltaMovement.lengthSqr() == 0)
			return null;

		return Direction.getApproximateNearest(deltaMovement.x, 0, deltaMovement.z);
	}
}
