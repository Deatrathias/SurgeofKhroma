package net.deatrathias.khroma.blocks;

import net.deatrathias.khroma.khroma.KhromaDeviceTier;

public interface IKhromaDevice {
	default KhromaDeviceTier getTier() {
		return KhromaDeviceTier.BASIC;
	}
}
