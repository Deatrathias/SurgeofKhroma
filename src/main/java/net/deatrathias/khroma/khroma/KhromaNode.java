package net.deatrathias.khroma.khroma;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class KhromaNode implements INBTSerializable<CompoundTag> {
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

	public KhromaNode(Provider provider, CompoundTag nbt) {
		deserializeNBT(provider, nbt);
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(Provider provider) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("khroma", khroma.asInt());
		nbt.putInt("level", level);
		nbt.putLong("position", position.asLong());
		return nbt;
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag nbt) {
		khroma = Khroma.fromInt(nbt.getInt("khroma").orElse(0));
		level = nbt.getInt("level").orElse(1);
		position = BlockPos.of(nbt.getLong("position").orElse(0L));
	}
}
