package net.deatrathias.khroma.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record BlockDirection(BlockPos pos, Direction direction) {
	public static final Codec<BlockDirection> CODEC = RecordCodecBuilder.create(
			app -> app.group(
					BlockPos.CODEC.fieldOf("pos").forGetter(BlockDirection::pos),
					Direction.CODEC.fieldOf("direction").forGetter(BlockDirection::direction))
					.apply(app, BlockDirection::new));

}
