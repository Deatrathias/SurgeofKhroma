package net.deatrathias.khroma.items;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.SoundReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class WarpCanisterItem extends Item {

	public static record ContainerLinkLocation(GlobalPos globalPos, Direction face, Component containerName) implements TooltipProvider {
		public static final Codec<ContainerLinkLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				GlobalPos.CODEC.fieldOf("globalPos").forGetter(ContainerLinkLocation::globalPos),
				Direction.CODEC.fieldOf("face").forGetter(ContainerLinkLocation::face),
				ComponentSerialization.CODEC.fieldOf("containerName").forGetter(ContainerLinkLocation::containerName))
				.apply(instance, ContainerLinkLocation::new));
		public static final StreamCodec<ByteBuf, ContainerLinkLocation> STREAM_CODEC = StreamCodec.composite(
				GlobalPos.STREAM_CODEC, ContainerLinkLocation::globalPos,
				Direction.STREAM_CODEC, ContainerLinkLocation::face,
				ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC, ContainerLinkLocation::containerName,
				ContainerLinkLocation::new);

		@Override
		public void addToTooltip(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag flag, DataComponentGetter componentGetter) {
			tooltipAdder.accept(containerName.copy().withStyle(ChatFormatting.GRAY));
			tooltipAdder.accept(
					Component.translatable(globalPos.dimension().location().toLanguageKey("dimension")).append(": [").append(globalPos.pos().toShortString()).append("]")
							.withStyle(ChatFormatting.GRAY));
		}
	}

	public WarpCanisterItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		ContainerLinkLocation component = stack.get(RegistryReference.DATA_COMPONENT_CONTAINER_LINK_LOCATION);
		if (component != null)
			component.addToTooltip(context, tooltipAdder, flag, stack);
		else
			tooltipAdder.accept(Component.translatable(descriptionId + ".unbound").withStyle(ChatFormatting.RED));
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (other.isEmpty())
			return false;
		ContainerLinkLocation component = stack.get(RegistryReference.DATA_COMPONENT_CONTAINER_LINK_LOCATION);
		if (component == null)
			return false;

		Level level;
		if (player instanceof ServerPlayer) {
			level = player.getServer().getLevel(component.globalPos.dimension());

			if (level == null)
				return false;

			IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, component.globalPos.pos(), component.face);
			if (handler == null) {
				player.displayClientMessage(Component.translatable(descriptionId + ".fail").withStyle(ChatFormatting.RED), false);
				stack.remove(RegistryReference.DATA_COMPONENT_CONTAINER_LINK_LOCATION);
				return true;
			}

			ItemStack remaining = action == ClickAction.PRIMARY ? other : other.copyWithCount(1);
			int count = remaining.getCount();
			int leftCount = action == ClickAction.PRIMARY ? 0 : other.getCount() - 1;
			for (int i = 0; i < handler.getSlots(); i++) {
				remaining = handler.insertItem(i, remaining, false);
				if (remaining.isEmpty())
					break;
			}

			if (remaining.getCount() == count)
				return false;

			if (remaining.isEmpty() && leftCount > 0)
				remaining = other.copyWithCount(leftCount);

			if (access != null)
				access.set(remaining);
			else
				slot.set(remaining);
			broadcastChangesOnContainerMenu(player);
			player.playNotifySound(SoundReference.WARP_CANISTER_SEND.get(), SoundSource.PLAYERS, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
		}

		return true;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		return overrideOtherStackedOnMe(stack, slot.getItem(), slot, action, player, null);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.isSecondaryUseActive())
			return InteractionResult.PASS;

		IItemHandler handler = context.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, context.getClickedPos(), context.getClickedFace());

		if (handler != null && handler.getSlots() > 0) {
			if (!context.getLevel().isClientSide) {
				BlockState state = context.getLevel().getBlockState(context.getClickedPos());
				Component name;
				BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
				if (be != null && be instanceof Nameable container)
					name = container.getDisplayName();
				else
					name = state.getBlock().getName();

				context.getItemInHand().set(RegistryReference.DATA_COMPONENT_CONTAINER_LINK_LOCATION,
						new ContainerLinkLocation(GlobalPos.of(context.getLevel().dimension(), context.getClickedPos()), context.getClickedFace(), name));

				context.getPlayer().displayClientMessage(Component.translatable(descriptionId + ".connected", name, context.getClickedFace().getName()), true);
			}
			context.getPlayer().playSound(SoundReference.WARP_CANISTER_CONNECT.get(), 0.8F, 1F);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private void broadcastChangesOnContainerMenu(Player player) {
		AbstractContainerMenu abstractcontainermenu = player.containerMenu;
		if (abstractcontainermenu != null) {
			abstractcontainermenu.slotsChanged(player.getInventory());
		}
	}
}
