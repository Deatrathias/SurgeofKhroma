package net.deatrathias.khroma.entities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.entities.renderer.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.particles.KhromaParticleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class KhromaNodeEntity extends Entity {

	public static final EntityDataAccessor<Integer> KHROMA = SynchedEntityData.defineId(KhromaNodeEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(KhromaNodeEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> FORCE_VISIBLE = SynchedEntityData.defineId(KhromaNodeEntity.class, EntityDataSerializers.BOOLEAN);

	public KhromaNodeEntity(EntityType<KhromaNodeEntity> entityType, Level level) {
		super(entityType, level);
		setInvulnerable(true);
	}

	public static KhromaNodeEntity create(Level level, BlockPos blockPos, Khroma nodeKhroma, int nodeLevel) {
		KhromaNodeEntity result = new KhromaNodeEntity(RegistryReference.ENTITY_KHROMA_NODE.get(), level);
		SynchedEntityData data = result.getEntityData();
		result.setPos(blockPos.getBottomCenter());
		data.set(KHROMA, nodeKhroma.asInt());
		data.set(LEVEL, nodeLevel);
		data.set(FORCE_VISIBLE, false);
		return result;
	}

	@Override
	protected void defineSynchedData(Builder builder) {
		builder.define(KHROMA, 0);
		builder.define(LEVEL, 1);
		builder.define(FORCE_VISIBLE, false);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		SynchedEntityData data = getEntityData();
		data.set(KHROMA, input.getIntOr("khroma", 0));
		data.set(LEVEL, input.getIntOr("level", 1));
		data.set(FORCE_VISIBLE, input.getBooleanOr("force_visible", false));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		SynchedEntityData data = getEntityData();
		output.putInt("khroma", data.get(KHROMA));
		output.putInt("level", data.get(LEVEL));
		output.putBoolean("force_visible", data.get(FORCE_VISIBLE));
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

	@Override
	public boolean canBeHitByProjectile() {
		return false;
	}

	@Override
	public boolean canTeleport(Level oldLevel, Level newLevel) {
		return false;
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemBySlot(LivingEntity.getSlotForHand(hand));
		if (stack != null && stack.is(RegistryReference.ITEM_BLOCK_NODE_COLLECTOR)) {
			return stack.useOn(new UseOnContext(player, hand, new BlockHitResult(getEyePosition(), Direction.DOWN, new BlockPos(getOnPos()), false)));
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		if (getEntityData().get(FORCE_VISIBLE))
			return true;
		if (KhromaNodeEntityRenderer.canSeeNodes())
			return super.shouldRender(x, y, z);
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 65536;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			generateParticles();
		}
	}

	private void generateParticles() {
		Vec3 camPos = Minecraft.getInstance().getCameraEntity().position();
		if (!shouldRender(camPos.x, camPos.y, camPos.z) || getEntityData().get(FORCE_VISIBLE))
			return;
		Vec3 pos = new Vec3(getX(), getY() + 0.5, getZ());
		if (camPos.distanceToSqr(pos) < 4096) {
			Vec3 vector = new Vec3(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).normalize();
			pos = pos.add(vector.scale(random.nextDouble() * 0.5 + 0.3));
			Vec3 speed = vector.scale(random.nextDouble() * 0.1 + 0.05);
			level().addParticle(new KhromaParticleOption(getKhroma()), pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
		}
	}

	public Khroma getKhroma() {
		return Khroma.fromInt(getEntityData().get(KHROMA));
	}

	public int getNodeLevel() {
		return getEntityData().get(LEVEL);
	}

	public void setForceVisible(boolean forceVisible) {
		getEntityData().set(FORCE_VISIBLE, forceVisible);
	}
}
