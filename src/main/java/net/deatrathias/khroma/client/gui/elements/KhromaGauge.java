package net.deatrathias.khroma.client.gui.elements;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.KhromaMaterials;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

public class KhromaGauge implements Renderable, LayoutElement {

	private int x, y;

	private Khroma khroma;

	private TextureAtlasSprite currentTexture;

	private Font font;

	private float consumed;

	private float softLimit;

	private float effective;

	private List<Component> tooltipComponents;

	public void init(Minecraft minecraft) {
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

		setTexture();
	}

	public void setRates(float consumed, float softLimit, float effective) {
		this.consumed = consumed;
		this.softLimit = softLimit;
		this.effective = effective;
	}

	private void setTexture() {
		currentTexture = Minecraft.getInstance().getAtlasManager().get(KhromaMaterials.getMaterial(khroma));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (currentTexture == null)
			return;

		int color = khroma.getTint();

		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, currentTexture, x, y, 16, 16, color);
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, currentTexture, x, y + 16, 16, 16, color);
	}

	public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		tooltipComponents.clear();
		DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Minecraft.getInstance().getLocale()));
		tooltipComponents.add(Component.translatable(khroma.getLocalizedName()));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.consumed", format.format(consumed)));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.softLimit", format.format(softLimit)));
		tooltipComponents.add(Component.translatable("tooltip." + SurgeofKhroma.MODID + ".khroma_gauge.effective", format.format(effective)));
		guiGraphics.setComponentTooltipForNextFrame(font, tooltipComponents, mouseX, mouseY);
	}

	@Override
	public void visitWidgets(Consumer<AbstractWidget> consumer) {
	}
}
