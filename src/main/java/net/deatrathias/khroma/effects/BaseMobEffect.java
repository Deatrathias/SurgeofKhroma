package net.deatrathias.khroma.effects;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BaseMobEffect extends MobEffect {

	public BaseMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public BaseMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
		super(category, color, particle);
	}
}
