package net.deatrathias.khroma.registries;

import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.gui.KhromaImbuerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class UIReference {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SurgeofKhroma.MODID);
	/**
	 * 
	 * MENUS
	 * 
	 */
	public static final Supplier<MenuType<KhromaApertureMenu>> KHROMA_APERTURE = MENUS.register("khroma_aperture_menu",
			() -> new MenuType<KhromaApertureMenu>(KhromaApertureMenu::new, FeatureFlags.DEFAULT_FLAGS));
	public static final Supplier<MenuType<KhromaImbuerMenu>> KHROMA_IMBUER = MENUS.register("khroma_imbuer_menu",
	() -> new MenuType<KhromaImbuerMenu>(KhromaImbuerMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
