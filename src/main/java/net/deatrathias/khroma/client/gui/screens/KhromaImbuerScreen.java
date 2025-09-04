package net.deatrathias.khroma.client.gui.screens;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.client.gui.elements.KhromaGauge;
import net.deatrathias.khroma.gui.KhromaImbuerMenu;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class KhromaImbuerScreen extends AbstractContainerScreen<KhromaImbuerMenu> {

	private static final ResourceLocation TEXTURE = SurgeofKhroma.resource("textures/gui/container/khroma_imbuer.png");
	private static final ResourceLocation PROGRESS_SPRITE = SurgeofKhroma.resource("container/khroma_imbuer/progress");

	private KhromaGauge gauge;

	public KhromaImbuerScreen(KhromaImbuerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		super.init();
		gauge = new KhromaGauge(15, 25);
		gauge.init(minecraft);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int x = leftPos;
		int y = topPos;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE, 22, 10, 0, 0, x + 72, y + 36, Mth.ceil(22 * menu.getProgress()), 10);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderLabels(guiGraphics, mouseX, mouseY);
		KhromaThroughput throughput = menu.getKhromaThroughput();
		gauge.setKhroma(throughput.getKhroma());
		gauge.setRates(throughput.getRate(), menu.getSoftLimit(), menu.getEffectiveRate());
		gauge.render(guiGraphics, mouseX, mouseY, 0);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderTooltip(guiGraphics, mouseX, mouseY);
		if (isHovering(gauge.getX(), gauge.getY(), gauge.getWidth(), gauge.getHeight(), (double) mouseX, (double) mouseY))
			gauge.renderTooltip(guiGraphics, mouseX, mouseY);
	}
}
