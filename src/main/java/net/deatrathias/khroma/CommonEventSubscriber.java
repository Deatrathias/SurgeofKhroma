package net.deatrathias.khroma;

import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.khroma.IKhromaConsumerBlock;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.deatrathias.khroma.network.ServerboundSetApertureLimitPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonEventSubscriber {

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlock(RegistryReference.KHROMA_PROVIDER_BLOCK, (level, pos, state, be, side) -> (((IKhromaProviderBlock) level.getBlockEntity(pos)).getProvider(level, pos, state, side)),
				RegistryReference.BLOCK_KHROMA_PROVIDER.get(), RegistryReference.BLOCK_KHROMA_APERTURE.get(), RegistryReference.BLOCK_KHROMA_COMBINER.get(),
				RegistryReference.BLOCK_KHROMA_SEPARATOR.get(), RegistryReference.BLOCK_NODE_COLLECTOR.get());
		event.registerBlock(RegistryReference.KHROMA_CONSUMER_BLOCK, (level, pos, state, be, side) -> (((IKhromaConsumerBlock) level.getBlockEntity(pos)).getConsumer(level, pos, state, side)),
				RegistryReference.BLOCK_KHROMA_APERTURE.get(), RegistryReference.BLOCK_KHROMA_COMBINER.get(), RegistryReference.BLOCK_KHROMA_MACHINE.get(),
				RegistryReference.BLOCK_KHROMA_SEPARATOR.get());
		event.registerItem(CuriosCapability.ITEM, (stack, context) -> new ICurio() {
			@Override
			public ItemStack getStack() {
				return stack;
			}

			@Override
			public boolean canEquipFromUse(SlotContext slotContext) {
				return true;
			}
		}, RegistryReference.ITEM_CHROMATIC_GLASSES);
	}

	@SubscribeEvent
	private static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, RegistryReference.ATTRIBUTE_TELEPORT_DROPS);
	}

	@SubscribeEvent
	private static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.playToServer(ServerboundSetApertureLimitPacket.TYPE, ServerboundSetApertureLimitPacket.STREAM_CODEC, new MainThreadPayloadHandler<>((data, context) -> {
			Player player = context.player();
			if (player.containerMenu instanceof KhromaApertureMenu menu) {
				if (!player.containerMenu.stillValid(player)) {
					SurgeofKhroma.LOGGER.debug("Player {} interacted with invalid menu {}", player, player.containerMenu);
					return;
				}
				menu.setLimit(data.value());
			}
		}));
	}
}
