package net.deatrathias.khroma.items;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class SpannerItem extends Item implements IItemExtension {

	public static final ItemAbility SPANNER_ADJUST = ItemAbility.get("spanner_adjust");

	public enum SpannerColorLocation implements StringRepresentable {
		BASE("base"),
		MIDDLE("middle"),
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private SpannerColorLocation(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public static record SpannerColors(int colorBase, int colorMiddle, int colorTop, int colorBottom) {
		public static final Codec<SpannerColors> CODEC = RecordCodecBuilder.create(app -> app.group(
				Codec.INT.fieldOf("colorBase").forGetter(SpannerColors::colorBase),
				Codec.INT.fieldOf("colorMiddle").forGetter(SpannerColors::colorMiddle),
				Codec.INT.fieldOf("colorTop").forGetter(SpannerColors::colorTop),
				Codec.INT.fieldOf("colorBottom").forGetter(SpannerColors::colorBottom))
				.apply(app, SpannerColors::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SpannerColors> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.INT, SpannerColors::colorBase,
				ByteBufCodecs.INT, SpannerColors::colorMiddle,
				ByteBufCodecs.INT, SpannerColors::colorTop,
				ByteBufCodecs.INT, SpannerColors::colorBottom,
				SpannerColors::new);

		public int getColorFromLocation(SpannerColorLocation location) {
			switch (location) {
			case BASE:
				return colorBase;
			case MIDDLE:
				return colorMiddle;
			case TOP:
				return colorTop;
			case BOTTOM:
				return colorBottom;
			default:
				return 0;
			}
		}
	}

	public static Item.Properties spanner(Item.Properties properties) {
		var lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
		return properties.component(DataComponents.TOOL, new Tool(List.of(Tool.Rule.minesAndDrops(lookup.getOrThrow(TagReference.Blocks.KHROMA_DEVICES), 10f)), 1, 1, true));
	}

	public SpannerItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return itemAbility == SPANNER_ADJUST;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Optional<BlockState> optional = Optional.empty();

		if (state.is(BlockReference.KHROMA_LINE)) {
			optional = Optional.ofNullable(state.getToolModifiedState(context, SPANNER_ADJUST, false));
		} else if (optional.isEmpty()) {
			pos = pos.relative(context.getClickedFace());
			state = level.getBlockState(pos);
			if (state.is(BlockReference.KHROMA_LINE)) {
				optional = Optional.ofNullable(KhromaLineBlock.connect(level, state, pos, context.getClickedFace().getOpposite()));
			}

			if (optional.isEmpty()) {
				pos = context.getClickedPos();
				state = level.getBlockState(pos);
				optional = Optional.ofNullable(state.getToolModifiedState(context, SPANNER_ADJUST, false));
			}
		}

		if (optional.isPresent()) {
			Player player = context.getPlayer();
			if (!level.isClientSide) {
				level.setBlock(pos, optional.get(), 11);
				level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, optional.get()));
			}
			SoundType soundtype = state.getSoundType(level, pos, player);
			level.playSound(player, pos, state.getSoundType(level, pos, player).getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
