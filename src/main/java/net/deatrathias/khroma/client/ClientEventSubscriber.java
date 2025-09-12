package net.deatrathias.khroma.client;

import io.wispforest.accessories.Accessories;
import mezz.jei.api.constants.ModIds;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.client.gui.screens.KhromaApertureScreen;
import net.deatrathias.khroma.client.gui.screens.KhromaImbuerScreen;
import net.deatrathias.khroma.client.models.StrixModel;
import net.deatrathias.khroma.client.particles.KhromaParticle.KhromaParticleProvider;
import net.deatrathias.khroma.client.rendering.entities.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.client.rendering.entities.StrixRenderer;
import net.deatrathias.khroma.client.rendering.items.SpannerColorTint;
import net.deatrathias.khroma.compat.accessories.AccessoriesRegister;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.compat.jei.JeiKhromaPlugin;
import net.deatrathias.khroma.khroma.Khroma;
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
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
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

		if (ModList.get().isLoaded(Accessories.MODID))
			AccessoriesRegister.registerAccessoriesRenderer();
		else if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			CuriosRegister.registerCurioRenderer();

		configureRenderLayers();
	}

	@SuppressWarnings("deprecation")
	private static void configureRenderLayers() {
		for (var tree : BlockReference.IMBUED_TREES) {
			tree.ifPresent(TreeBlock.SAPLING, block -> ItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT));
			tree.ifPresent(TreeBlock.POTTED_SAPLING, block -> ItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT));
			tree.ifPresent(TreeBlock.DOOR, block -> ItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT));
			tree.ifPresent(TreeBlock.TRAPDOOR, block -> ItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT));
		}
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
		event.registerLayerDefinition(ClientOnlyReference.BLOOMTREE_BOAT, BoatModel::createBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.BLOOMTREE_CHEST_BOAT, BoatModel::createChestBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.FLOWTREE_BOAT, BoatModel::createBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.FLOWTREE_CHEST_BOAT, BoatModel::createChestBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.SKYTREE_BOAT, BoatModel::createBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.SKYTREE_CHEST_BOAT, BoatModel::createChestBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.GRIMTREE_BOAT, BoatModel::createBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.GRIMTREE_CHEST_BOAT, BoatModel::createChestBoatModel);
		event.registerLayerDefinition(ClientOnlyReference.STRIX, StrixModel::createBodyLayer);
	}

	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityReference.KHROMA_NODE.get(), KhromaNodeEntityRenderer::new);
		event.registerEntityRenderer(BlockReference.SPARKTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SPARKTREE_BOAT));
		event.registerEntityRenderer(BlockReference.SPARKTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SPARKTREE_CHEST_BOAT));
		event.registerEntityRenderer(BlockReference.BLOOMTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.BLOOMTREE_BOAT));
		event.registerEntityRenderer(BlockReference.BLOOMTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.BLOOMTREE_CHEST_BOAT));
		event.registerEntityRenderer(BlockReference.FLOWTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.FLOWTREE_BOAT));
		event.registerEntityRenderer(BlockReference.FLOWTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.FLOWTREE_CHEST_BOAT));
		event.registerEntityRenderer(BlockReference.SKYTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SKYTREE_BOAT));
		event.registerEntityRenderer(BlockReference.SKYTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.SKYTREE_CHEST_BOAT));
		event.registerEntityRenderer(BlockReference.GRIMTREE.getBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.GRIMTREE_BOAT));
		event.registerEntityRenderer(BlockReference.GRIMTREE.getChestBoatEntity().get(), context -> new BoatRenderer(context, ClientOnlyReference.GRIMTREE_CHEST_BOAT));
		event.registerEntityRenderer(EntityReference.STRIX.get(), StrixRenderer::new);
		event.registerEntityRenderer(EntityReference.PLACED_ITEM.get(), ItemEntityRenderer::new);
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
