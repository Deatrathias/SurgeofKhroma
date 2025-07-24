package net.deatrathias.khroma.khroma;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class KhromaNode implements ValueIOSerializable {
	private BlockPos position;

	private Khroma khroma;

	private int level;

	public BlockPos getPosition() {
		return position;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	public int getLevel() {
		return level;
	}

	public KhromaNode(BlockPos position, Khroma khroma, int level) {
		this.position = position;
		this.khroma = khroma;
		this.level = level;
	}

	public KhromaNode(ValueInput input) {
		deserialize(input);
	}

	@Override
	public void serialize(ValueOutput output) {
		output.putInt("khroma", khroma.asInt());
		output.putInt("level", level);
		output.putLong("position", position.asLong());
	}

	@Override
	public void deserialize(ValueInput input) {
		khroma = Khroma.fromInt(input.getIntOr("khroma", 0));
		level = input.getIntOr("level", 1);
		position = BlockPos.of(input.getLongOr("position", 0L));
	}
}
