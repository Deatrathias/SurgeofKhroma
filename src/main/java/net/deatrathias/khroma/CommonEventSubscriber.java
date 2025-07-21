package net.deatrathias.khroma;

import net.deatrathias.khroma.blocks.KhrometalBlackBlock;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.network.ServerboundSetApertureLimitPacket;
import net.deatrathias.khroma.network.ServerboundWalkOnBlackKhrometalPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosResources;

@EventBusSubscriber(modid = SurgeofKhroma.MODID)
public final class CommonEventSubscriber {

	@SubscribeEvent
	private static void commonSetup(FMLCommonSetupEvent event) {

	}

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RegistryReference.BLOCK_ENTITY_KHROMA_IMBUER.get(), SidedInvWrapper::new);
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			CuriosRegister.registerCurioCapabilities(event);
	}

	@SubscribeEvent
	private static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, RegistryReference.ATTRIBUTE_TELEPORT_DROPS);
	}

	@SubscribeEvent
	private static void registerPackets(final RegisterPayloadHandlersEvent event) {
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
		registrar.playToServer(ServerboundWalkOnBlackKhrometalPacket.TYPE, ServerboundWalkOnBlackKhrometalPacket.STREAM_CODEC, new MainThreadPayloadHandler<>((data, context) -> {
			Player player = context.player();
			if (player.getBlockStateOn().is(RegistryReference.BLOCK_KHROMETAL_BLOCK_BLACK)) {
				((KhrometalBlackBlock) RegistryReference.BLOCK_KHROMETAL_BLOCK_BLACK.get()).doTeleport(player.level(), player.getOnPos(), player, data.direction());
			}
		}));
	}

	@SubscribeEvent
	private static void onDatapackSync(OnDatapackSyncEvent event) {
		event.sendRecipes(RegistryReference.RECIPE_KHROMA_IMBUEMENT.get());
	}
}
