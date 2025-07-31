package net.deatrathias.khroma;

import mezz.jei.api.constants.ModIds;
import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.compat.jei.JeiKhromaPlugin;
import net.deatrathias.khroma.entities.renderer.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.gui.KhromaApertureScreen;
import net.deatrathias.khroma.gui.KhromaImbuerScreen;
import net.deatrathias.khroma.items.renderer.SpannerColorTint;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.particles.KhromaParticle.KhromaParticleProvider;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.EntityReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.UIReference;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
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

@EventBusSubscriber(modid = SurgeofKhroma.MODID, value = Dist.CLIENT)
public class ClientEventSubscriber {
	@SubscribeEvent
	private static void onClientSetup(FMLClientSetupEvent event) {
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			CuriosRegister.registerCurioRenderer();
		configureRenderLayers();
	}

	@SuppressWarnings("deprecation")
	private static void configureRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(BlockReference.SPARKTREE.get(TreeBlock.SAPLING), ChunkSectionLayer.CUTOUT);
		ItemBlockRenderTypes.setRenderLayer(BlockReference.SPARKTREE.get(TreeBlock.POTTED_SAPLING), ChunkSectionLayer.CUTOUT);
		ItemBlockRenderTypes.setRenderLayer(BlockReference.SPARKTREE.get(TreeBlock.DOOR), ChunkSectionLayer.CUTOUT);
		ItemBlockRenderTypes.setRenderLayer(BlockReference.SPARKTREE.get(TreeBlock.TRAPDOOR), ChunkSectionLayer.CUTOUT);
	}

	@SubscribeEvent
	private static void registerBlockColorHandles(RegisterColorHandlersEvent.Block event) {
		event.register(((state, level, pos, tintIndex) -> {
			Khroma khroma = state.getValue(KhromaLineBlock.KHROMA);
			return khroma.getTint();
		}), BlockReference.KHROMA_LINE.get(), BlockReference.KHROMA_DISSIPATOR.get());
	}

	@SubscribeEvent
	private static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(SurgeofKhroma.resource("spanner_color"), SpannerColorTint.CODEC);
	}

	@SubscribeEvent
	private static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(UIReference.KHROMA_APERTURE.get(), KhromaApertureScreen::new);
		event.register(UIReference.KHROMA_IMBUER.get(), KhromaImbuerScreen::new);
	}

	@SubscribeEvent
	private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClientOnlyReference.SPARKTREE_BOAT, BoatModel::createBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.SPARKTREE_CHEST_BOAT, BoatModel::createChestBoatModel);
	}

	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityReference.KHROMA_NODE.get(), KhromaNodeEntityRenderer::new);
		event.registerEntityRenderer(BlockReference.SPARKTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SPARKTREE_BOAT));
		event.registerEntityRenderer(BlockReference.SPARKTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SPARKTREE_CHEST_BOAT));
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
		}, BlockReference.KHROMA_LINE, BlockReference.KHROMA_DISSIPATOR);
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
