package net.deatrathias.khroma;

import mezz.jei.api.constants.ModIds;
import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.compat.jei.JeiKhromaPlugin;
import net.deatrathias.khroma.entities.renderer.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.gui.KhromaApertureScreen;
import net.deatrathias.khroma.gui.KhromaImbuerScreen;
import net.deatrathias.khroma.items.renderer.SpannerColorTint;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.particles.KhromaParticle.KhromaParticleProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import top.theillusivec4.curios.api.CuriosResources;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = SurgeofKhroma.MODID, value = Dist.CLIENT)
public class ClientEventSubscriber {
	@SubscribeEvent
	private static void onClientSetup(FMLClientSetupEvent event) {
		SurgeofKhroma.LOGGER.info("client started");
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			CuriosRegister.registerCurioRenderer();
	}

	@SubscribeEvent
	private static void registerBlockColorHandles(RegisterColorHandlersEvent.Block event) {
		event.register(((state, level, pos, tintIndex) -> {
			Khroma khroma = state.getValue(KhromaLineBlock.KHROMA);
			return khroma.getTint();
		}), RegistryReference.BLOCK_KHROMA_LINE.get(), RegistryReference.BLOCK_KHROMA_DISSIPATOR.get());
	}

	@SubscribeEvent
	private static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(SurgeofKhroma.resource("spanner_color"), SpannerColorTint.CODEC);
	}

	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(RegistryReference.MENU_KHROMA_APERTURE.get(), KhromaApertureScreen::new);
		event.register(RegistryReference.MENU_KHROMA_IMBUER.get(), KhromaImbuerScreen::new);
	}

	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(RegistryReference.ENTITY_KHROMA_NODE.get(), KhromaNodeEntityRenderer::new);
	}

	@SubscribeEvent
	private static void recipesReceived(RecipesReceivedEvent event) {
		if (ModList.get().isLoaded(ModIds.JEI_ID)) {
			JeiKhromaPlugin.setRecipes(event.getRecipeMap());
		}
	}

	@SubscribeEvent
	private static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
		if (ModList.get().isLoaded(ModIds.JEI_ID)) {
			JeiKhromaPlugin.setRecipes(RecipeMap.EMPTY);
		}
	}

	@SubscribeEvent
	private static void registerExtensions(RegisterClientExtensionsEvent event) {
		event.registerBlock(new IClientBlockExtensions() {
			@Override
			public boolean areBreakingParticlesTinted(BlockState state, ClientLevel level, BlockPos pos) {
				return false;
			}
		}, RegistryReference.BLOCK_KHROMA_LINE, RegistryReference.BLOCK_KHROMA_DISSIPATOR);
	}

	@SubscribeEvent
	private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(RegistryReference.PARTICLE_KHROMA.get(), new KhromaParticleProvider());
	}

	@SubscribeEvent
	private static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ClientOnlyReference.PIPELINE_KHROMA_NODE);
	}
}
