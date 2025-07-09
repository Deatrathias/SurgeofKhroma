package net.deatrathias.khroma;

import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.entities.renderer.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.gui.KhromaApertureScreen;
import net.deatrathias.khroma.items.renderer.ChromaticGlassesRenderer;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.RecipeBookCategories;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		SurgeofKhroma.LOGGER.info("client started");
		CuriosRendererRegistry.register(RegistryReference.ITEM_CHROMATIC_GLASSES.get(), () -> new ChromaticGlassesRenderer());
	}

	@SubscribeEvent
	public static void registerBlockColorHandles(RegisterColorHandlersEvent.Block event) {
		event.register(((state, level, pos, tintIndex) -> {
			Integer khroma = state.getValue(KhromaLineBlock.KHROMA);
			return Khroma.KhromaColors[khroma];
		}), RegistryReference.BLOCK_KHROMA_LINE.value());
	}

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(RegistryReference.MENU_KHROMA_APERTURE.get(), KhromaApertureScreen::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(RegistryReference.ENTITY_KHROMA_NODE.get(), KhromaNodeEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
		event.registerRecipeCategoryFinder(RegistryReference.RECIPE_KHROMA_IMBUEMENT.get(), (recipe) -> RecipeBookCategories.CRAFTING_MISC);
	}
}
