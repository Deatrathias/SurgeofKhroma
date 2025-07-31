package net.deatrathias.khroma.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class KhromaParticleOption implements ParticleOptions {

	public static final MapCodec<KhromaParticleOption> CODEC = RecordCodecBuilder
			.mapCodec(instance -> instance.group(Khroma.CODEC.fieldOf("khroma").forGetter(KhromaParticleOption::getKhroma)).apply(instance, KhromaParticleOption::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KhromaParticleOption> STREAM_CODEC = StreamCodec.composite(Khroma.STREAM_CODEC, KhromaParticleOption::getKhroma,
			KhromaParticleOption::new);

	private final Khroma khroma;

	public KhromaParticleOption(Khroma khroma) {
		super();
		this.khroma = khroma;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	@Override
	public ParticleType<?> getType() {
		return RegistryReference.PARTICLE_KHROMA.get();
	}

}
