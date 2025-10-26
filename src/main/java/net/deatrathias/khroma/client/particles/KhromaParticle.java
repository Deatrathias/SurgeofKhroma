package net.deatrathias.khroma.client.particles;

import net.deatrathias.khroma.client.KhromaMaterials;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class KhromaParticle extends SingleQuadParticle {
	private final float uo;
	private final float vo;
	private final float startingSize;

	public KhromaParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Khroma khroma) {
		super(level, x, y, z, Minecraft.getInstance().getAtlasManager().get(KhromaMaterials.getMaterial(khroma)));
		this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
		setColor(khroma.getTint());
		this.quadSize *= random.nextFloat() * 0.4f;
		startingSize = quadSize;
		this.uo = this.random.nextFloat() * 3.0F;
		this.vo = this.random.nextFloat() * 3.0F;
		this.hasPhysics = false;
	}

	private void setColor(int color) {
		setColor(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
	}

	@Override
	protected Layer getLayer() {
		return SingleQuadParticle.Layer.TERRAIN;
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

	public static class KhromaParticleProvider implements ParticleProvider<KhromaParticleOption> {

		@Override
		public Particle createParticle(KhromaParticleOption particleType, ClientLevel level, double x, double y,
				double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			Particle particle = new KhromaParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, particleType.getKhroma());
			particle.setLifetime(level.random.nextInt(15) + 10);
			return particle;
		}

	}
}
