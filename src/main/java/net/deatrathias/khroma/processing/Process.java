package net.deatrathias.khroma.processing;

import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class Process {
	protected ProcessType<?> type;

	public Process(ProcessType<?> type) {
		this.type = type;
	}

	protected abstract Khroma requiredKhroma();

	public abstract void load(ValueInput input);

	public abstract void save(ValueOutput output);

	public void saveWithId(ValueOutput output) {
		output.store("id", ProcessType.CODEC, type);
		save(output);
	}
}
