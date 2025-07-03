package net.deatrathias.khroma.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class KhromaApertureScreen extends AbstractContainerScreen<KhromaApertureMenu> {
	private static final ResourceLocation KHROMA_APERTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(SurgeofKhroma.MODID, "textures/gui/container/khroma_aperture.png");
	private Player player;
	private EditBox name;
	private float limit;

	public KhromaApertureScreen(KhromaApertureMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		player = playerInventory.player;
	}

	@Override
	protected void init() {
		super.init();
		name = new EditBox(font, leftPos, topPos, title);
	}

	@Override
	protected void containerTick() {
		limit = menu.getLimit();
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(KHROMA_APERTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Background(this, guiGraphics, mouseX, mouseY));
		for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
			renderable.render(guiGraphics, mouseX, mouseY, partialTick);
		}

		RenderSystem.disableDepthTest();
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate((float) leftPos, (float) topPos, 0.0F);
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
		guiGraphics.drawString(font, Float.toString(limit), 10, 100, 0x404040);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Foreground(this, guiGraphics, mouseX, mouseY));

		guiGraphics.pose().popPose();
		RenderSystem.enableDepthTest();

	}
}
