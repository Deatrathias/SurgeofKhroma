package net.deatrathias.khroma.entities;

import net.deatrathias.khroma.registries.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;

public class PlacedItemEntity extends ItemEntity {

	public PlacedItemEntity(EntityType<PlacedItemEntity> entityType, Level level) {
		super(entityType, level);
	}

	public PlacedItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
		this(EntityReference.PLACED_ITEM.get(), level);
		this.setPos(posX, posY, posZ);
		this.setDeltaMovement(0, 0, 0);
		this.setItem(itemStack);
	}

	@Override
	public void tick() {
		if (getItem().onEntityItemUpdate(this))
			return;
		if (this.getItem().isEmpty()) {
			this.discard();
			return;
		}

		baseTick();

		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();

		if (this.age != -32768) {
			this.age++;
		}
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	public void setItem2(ItemStack stack) {
		var entityData = getEntityData();
		ItemStack existing = entityData.get(DATA_ITEM);
		entityData.set(DATA_ITEM, stack, !ItemStack.matches(stack, existing));

	}

	@Override
	public boolean hasPickUpDelay() {
		return true;
	}
}
