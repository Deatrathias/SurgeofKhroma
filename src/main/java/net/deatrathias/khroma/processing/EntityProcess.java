package net.deatrathias.khroma.processing;

import net.minecraft.world.entity.Entity;

public abstract class EntityProcess extends BaseProcess {

	public EntityProcess(ProcessType<?> type) {
		super(type);
	}

	public abstract boolean processEntity(float potency, Entity entity);
}
