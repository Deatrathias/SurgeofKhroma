package net.deatrathias.khroma.network;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundSetApertureLimitPacket(float value) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ServerboundSetApertureLimitPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "set_aperture_limit"));

	public static final StreamCodec<FriendlyByteBuf, ServerboundSetApertureLimitPacket> STREAM_CODEC = StreamCodec.ofMember(ServerboundSetApertureLimitPacket::write,
			ServerboundSetApertureLimitPacket::new);

	public ServerboundSetApertureLimitPacket(float value) {
		this.value = value;
	}

	public ServerboundSetApertureLimitPacket(FriendlyByteBuf buffer) {
		this(buffer.readFloat());
	}

	private void write(FriendlyByteBuf buffer) {
		buffer.writeFloat(value);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
