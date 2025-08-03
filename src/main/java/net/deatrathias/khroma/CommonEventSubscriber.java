package net.deatrathias.khroma;

import net.deatrathias.khroma.blocks.khrometal.KhrometalBlackBlock;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.network.ServerboundSetApertureLimitPacket;
import net.deatrathias.khroma.network.ServerboundWalkOnBlackKhrometalPacket;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.RecipeReference;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
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
	private static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(BlockReference::configureExtra);
	}

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockReference.BE_KHROMA_IMBUER.get(), SidedInvWrapper::new);
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			CuriosRegister.registerCurioCapabilities(event);
	}

	@SubscribeEvent
	private static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, RegistryReference.ATTRIBUTE_TELEPORT_DROPS);
		event.add(EntityType.PLAYER, RegistryReference.ATTRIBUTE_CAN_SEE_NODES);
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
			if (player.getBlockStateOn().is(BlockReference.KHROMETAL_BLOCK_BLACK)) {
				((KhrometalBlackBlock) BlockReference.KHROMETAL_BLOCK_BLACK.get()).doTeleport(player.level(), player.getOnPos(), player, data.direction());
			}
		}));
	}

	@SubscribeEvent
	private static void onDatapackSync(OnDatapackSyncEvent event) {
		event.sendRecipes(RecipeReference.KHROMA_IMBUEMENT.get());
	}

	@SubscribeEvent
	private static void addBlockToBlockEntities(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.SIGN, BlockReference.IMBUED_TREES.stream().filter(tree -> tree.get(TreeBlock.SIGN) != null).mapMulti((tree, consumer) -> {
			consumer.accept(tree.get(TreeBlock.SIGN));
			consumer.accept(tree.get(TreeBlock.WALL_SIGN));
		}).toArray(Block[]::new));
		event.modify(BlockEntityType.HANGING_SIGN, BlockReference.IMBUED_TREES.stream().filter(tree -> tree.get(TreeBlock.HANGING_SIGN) != null).mapMulti((tree, consumer) -> {
			consumer.accept(tree.get(TreeBlock.HANGING_SIGN));
			consumer.accept(tree.get(TreeBlock.WALL_HANGING_SIGN));
		}).toArray(Block[]::new));
	}
}
