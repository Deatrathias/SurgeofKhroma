package net.deatrathias.khroma.client.gui.screens;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.network.ServerboundSetApertureLimitPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class KhromaApertureScreen extends AbstractContainerScreen<KhromaApertureMenu> {
	private static final ResourceLocation KHROMA_APERTURE_LOCATION = SurgeofKhroma.resource("textures/gui/container/khroma_aperture.png");
	private ExtendedSlider slider;

	private class Slider extends ExtendedSlider {

		public Slider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
			super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1, 0, drawString);
		}

		@Override
		public void onRelease(double mouseX, double mouseY) {
			minecraft.getConnection().send(new ServerboundSetApertureLimitPacket((float) this.value));
			super.onRelease(mouseX, mouseY);
		}
	}

	public KhromaApertureScreen(KhromaApertureMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		super.init();
		slider = new Slider(leftPos + 16, topPos + 18, 89, 11, Component.empty(), Component.literal("%"), 0, 100, menu.getLimit(), true);
		addRenderableWidget(slider);
	}

	public void updateSlider(float value) {
		if (getFocused() == slider && isDragging())
			return;

		slider.setValue(value * 100f);
	}

	@Override
	protected void containerTick() {
		updateSlider(menu.getLimit());
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, KHROMA_APERTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		boolean result = this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) : false;
		if (result)
			return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
			renderable.render(guiGraphics, mouseX, mouseY, partialTick);
		}

		guiGraphics.pose().pushMatrix();
		guiGraphics.pose().translate((float) leftPos, (float) topPos);
		renderLabels(guiGraphics, mouseX, mouseY);

		guiGraphics.pose().popMatrix();

	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
	}
}
