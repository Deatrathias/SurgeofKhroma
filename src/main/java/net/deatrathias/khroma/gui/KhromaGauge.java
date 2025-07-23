package net.deatrathias.khroma.gui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KhromaGauge implements Renderable, LayoutElement {

	private static final ResourceLocation TEXTURE_KHROMA = SurgeofKhroma.resource("khroma/khroma");
	private static final ResourceLocation TEXTURE_KHROMA_SPECTRUM = SurgeofKhroma.resource("khroma/khroma_spectrum");
	private static final ResourceLocation TEXTURE_KHROMA_LIGHT_SPECTRUM = SurgeofKhroma.resource("khroma/khroma_spectrum_white");
	private static final ResourceLocation TEXTURE_KHROMA_DARK_SPECTRUM = SurgeofKhroma.resource("khroma/khroma_spectrum_black");
	private static final ResourceLocation TEXTURE_KHROMA_KHROMEGA = SurgeofKhroma.resource("khroma/khroma_khromega");

	private int x, y;

	private Khroma khroma;

	private TextureAtlasSprite currentTexture;

	private GuiSpriteManager sprites;

	private Font font;

	private float consumed;

	private float softLimit;

	private float effective;

	private List<Component> tooltipComponents;

	public void init(Minecraft minecraft) {
		sprites = minecraft.getGuiSprites();
		font = minecraft.font;
	}

	public KhromaGauge(int x, int y) {
		this.x = x;
		this.y = y;
		tooltipComponents = new ArrayList<Component>(4);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 32;
	}

	public void setKhroma(Khroma khroma) {
		this.khroma = khroma;

		setTexture(getTexturePerKhroma(khroma));
	}

	public static ResourceLocation getTexturePerKhroma(Khroma khroma) {
		if (khroma == Khroma.KHROMA_SPECTRUM)
			return TEXTURE_KHROMA_SPECTRUM;
		else if (khroma == Khroma.KHROMA_LIGHT_SPECTRUM)
			return TEXTURE_KHROMA_LIGHT_SPECTRUM;
		else if (khroma == Khroma.KHROMA_DARK_SPECTRUM)
			return TEXTURE_KHROMA_DARK_SPECTRUM;
		else if (khroma == Khroma.KHROMA_KHROMEGA)
			return TEXTURE_KHROMA_KHROMEGA;
		else
			return TEXTURE_KHROMA;
	}

	public void setRates(float consumed, float softLimit, float effective) {
		this.consumed = consumed;
		this.softLimit = softLimit;
		this.effective = effective;
	}

	private void setTexture(ResourceLocation spriteLocation) {
		currentTexture = sprites.getSprite(spriteLocation);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (currentTexture == null)
			return;

		int color = khroma.getTint();

		guiGraphics.blitSprite(RenderType::guiTextured, currentTexture, x, y, 16, 16, color);
		guiGraphics.blitSprite(RenderType::guiTextured, currentTexture, x, y + 16, 16, 16, color);
	}

	public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		tooltipComponents.clear();
		DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Minecraft.getInstance().getLocale()));
		tooltipComponents.add(Component.translatable(khroma.getLocalizedName()));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.consumed", format.format(consumed)));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.softLimit", format.format(softLimit)));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.effective", format.format(effective)));
		guiGraphics.renderTooltip(font, tooltipComponents, Optional.empty(), mouseX, mouseY);
	}

	@Override
	public void visitWidgets(Consumer<AbstractWidget> consumer) {
	}
}
