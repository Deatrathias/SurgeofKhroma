package net.deatrathias.khroma.compat.jei;

import java.util.List;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.deatrathias.khroma.gui.KhromaGauge;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public class KhromaIngredientRenderer implements IIngredientRenderer<Khroma> {

	private GuiSpriteManager sprites;

	public KhromaIngredientRenderer() {
		sprites = Minecraft.getInstance().getGuiSprites();
	}

	@Override
	public void render(GuiGraphics guiGraphics, Khroma ingredient) {
		TextureAtlasSprite sprite = sprites.getSprite(KhromaGauge.getTexturePerKhroma(ingredient));
		int color = Khroma.KhromaColors[ingredient.asInt()];
		float blue = (color & 255) / 255f;
		float green = ((color >> 8) & 255) / 255f;
		float red = ((color >> 16) & 255) / 255f;
		float alpha = ((color >> 24) & 255) / 255f;
		guiGraphics.blit(0, 0, 0, 16, 16, sprite, red, green, blue, alpha);
	}

	@Override
	public List<Component> getTooltip(Khroma ingredient, TooltipFlag tooltipFlag) {
		return List.of(Component.translatable(ingredient.getLocalizedName()));
	}

}
