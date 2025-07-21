package net.deatrathias.khroma.particles;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KhromaParticle extends TextureSheetParticle {

	public static ResourceLocation getTexturePerKhroma(Khroma khroma) {
		if (khroma == Khroma.KHROMA_SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum");
		else if (khroma == Khroma.KHROMA_LIGHT_SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum_white");
		else if (khroma == Khroma.KHROMA_DARK_SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum_black");
		else if (khroma == Khroma.KHROMA_KHROMEGA)
			return SurgeofKhroma.resource("block/khroma_khromega");
		else
			return SurgeofKhroma.resource("block/khroma");
	}

	private final float uo;
	private final float vo;
	private final float startingSize;

	@SuppressWarnings("deprecation")
	public KhromaParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Khroma khroma) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(getTexturePerKhroma(khroma)));
		this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
		setColor(Khroma.KhromaColors[khroma.asInt()]);
		// this.quadSize /= (random.nextFloat() * 6f + 3f);
		this.quadSize *= random.nextFloat() * 0.4f;
		startingSize = quadSize;
		this.uo = this.random.nextFloat() * 3.0F;
		this.vo = this.random.nextFloat() * 3.0F;
	}

	private void setColor(int color) {
		setColor(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.TERRAIN_SHEET;
	}

	@Override
	protected float getU0() {
		return this.sprite.getU((this.uo + 1.0F) / 4.0F);
	}

	@Override
	protected float getU1() {
		return this.sprite.getU(this.uo / 4.0F);
	}

	@Override
	protected float getV0() {
		return this.sprite.getV(this.vo / 4.0F);
	}

	@Override
	protected float getV1() {
		return this.sprite.getV((this.vo + 1.0F) / 4.0F);
	}

	@Override
	public void tick() {
		float ratio = (float) age / lifetime;
		quadSize = Mth.lerp(ratio, startingSize, 0);
		super.tick();
	}

	@OnlyIn(Dist.CLIENT)
	public static class KhromaParticleProvider implements ParticleProvider<KhromaParticleOption> {

		@Override
		public Particle createParticle(KhromaParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			Particle particle = new KhromaParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type.getKhroma());
			particle.setLifetime(level.random.nextInt(15) + 10);
			return particle;
		}

	}
}
