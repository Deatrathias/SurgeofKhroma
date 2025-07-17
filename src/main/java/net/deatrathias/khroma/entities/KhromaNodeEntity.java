package net.deatrathias.khroma.entities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

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
	protected void readAdditionalSaveData(CompoundTag compound) {
		SynchedEntityData data = getEntityData();
		data.set(KHROMA, compound.getInt("khroma").orElse(0));
		data.set(LEVEL, compound.getInt("level").orElse(1));
		data.set(FORCE_VISIBLE, compound.getBoolean("force_visible").orElse(false));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		SynchedEntityData data = getEntityData();
		compound.putInt("khroma", data.get(KHROMA));
		compound.putInt("level", data.get(LEVEL));
		compound.putBoolean("force_visible", data.get(FORCE_VISIBLE));
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
	@OnlyIn(Dist.CLIENT)
	public boolean shouldRender(double x, double y, double z) {
		if (getEntityData().get(FORCE_VISIBLE))
			return true;
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return false;
		var inventory = CuriosApi.getCuriosInventory(player);
		if (inventory.isEmpty())
			return false;
		if (inventory.get().isEquipped(RegistryReference.ITEM_CHROMATIC_GLASSES.get()))
			return super.shouldRender(x, y, z);
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 65536;
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
