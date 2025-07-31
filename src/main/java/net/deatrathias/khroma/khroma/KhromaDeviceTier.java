package net.deatrathias.khroma.khroma;

import net.minecraft.util.StringRepresentable;

public enum KhromaDeviceTier implements StringRepresentable {
	BASIC("basic", 20f),
	EXPERT("expert", 50f),
	MASTER("master", 5000),
	LEGENDARY("legendary", 2000f);

	private final String prefix;

	private final float softLimit;

	public static final StringRepresentable.EnumCodec<KhromaDeviceTier> CODEC = StringRepresentable.fromEnum(KhromaDeviceTier::values);

	private KhromaDeviceTier(String prefix, float softLimit) {
		this.prefix = prefix;
		this.softLimit = softLimit;
	}

	public String getPrefix() {
		return prefix;
	}

	public float getSoftLimit() {
		return softLimit;
	}

	@Override
	public String getSerializedName() {
		return prefix;
	}

}
