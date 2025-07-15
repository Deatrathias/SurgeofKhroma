package net.deatrathias.khroma.network;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundWalkOnBlackKhrometalPacket(Direction direction) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ServerboundWalkOnBlackKhrometalPacket> TYPE = new CustomPacketPayload.Type<>(SurgeofKhroma.resource("walk_on_black_khrometal"));

	public static final StreamCodec<FriendlyByteBuf, ServerboundWalkOnBlackKhrometalPacket> STREAM_CODEC = StreamCodec.ofMember(ServerboundWalkOnBlackKhrometalPacket::write,
			ServerboundWalkOnBlackKhrometalPacket::new);

	public ServerboundWalkOnBlackKhrometalPacket(FriendlyByteBuf buffer) {
		this(Direction.from2DDataValue(buffer.readUnsignedByte()));
	}

	private void write(FriendlyByteBuf buffer) {
		buffer.writeByte(direction.get2DDataValue());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
