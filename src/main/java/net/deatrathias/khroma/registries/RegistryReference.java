package net.deatrathias.khroma.registries;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.effects.PullDownMobEffect;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.deatrathias.khroma.particles.KhromaParticleOption;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RegistryReference {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SurgeofKhroma.MODID);
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SurgeofKhroma.MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SurgeofKhroma.MODID);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SurgeofKhroma.MODID);
	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SurgeofKhroma.MODID);

	/**
	 * 
	 * ATTRIBUTES
	 * 
	 */
	public static final Holder<Attribute> ATTRIBUTE_TELEPORT_DROPS = ATTRIBUTES.register("teleport_drops",
			() -> new BooleanAttribute("attribute." + SurgeofKhroma.MODID + ".teleport_drops", false).setSyncable(true));
	public static final Holder<Attribute> ATTRIBUTE_CAN_SEE_NODES = ATTRIBUTES.register("can_see_nodes",
			() -> new BooleanAttribute("attribute." + SurgeofKhroma.MODID + ".can_see_nodes", false).setSyncable(true));

	/**
	 * 
	 * DATA COMPONENTS
	 * 
	 */
	public static final Supplier<DataComponentType<SpannerItem.SpannerColors>> DATA_COMPONENT_SPANNER_COLORS = DATA_COMPONENT_TYPES.registerComponentType("spanner_colors",
			builder -> builder.persistent(SpannerItem.SpannerColors.CODEC).networkSynchronized(SpannerItem.SpannerColors.STREAM_CODEC));

	/**
	 * 
	 * ATTACHMENTS
	 * 
	 */
	public static final Supplier<AttachmentType<KhromaBiomeData>> KHROMA_BIOME_DATA = ATTACHMENT_TYPES.register("khroma_biome_data",
			() -> AttachmentType.serializable(() -> new KhromaBiomeData()).build());

	/**
	 * 
	 * DAMAGE TYPES
	 * 
	 */

	public static final ResourceKey<DamageType> DAMAGE_RED_KHROMETAL_BLOCK = ResourceKey.create(Registries.DAMAGE_TYPE, SurgeofKhroma.resource("red_khrometal_block"));

	/**
	 * 
	 * MOB EFFECTS
	 * 
	 */
	public static final Holder<MobEffect> EFFECT_TELEPORT_SICKNESS = MOB_EFFECTS.register("teleport_sickness",
			() -> new MobEffect(MobEffectCategory.HARMFUL, 0x00000000, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(0, 0))) {
			});
	public static final Holder<MobEffect> EFFECT_PULL_DOWN = MOB_EFFECTS.register("pull_down", () -> new PullDownMobEffect(MobEffectCategory.HARMFUL, 0xFF8d6700));

	/**
	 * 
	 * PARTICLES
	 * 
	 */
	public static final Supplier<ParticleType<KhromaParticleOption>> PARTICLE_KHROMA = PARTICLE_TYPES.register("particle_khroma",
			registryName -> RegistryReference.registerParticle(false, type -> KhromaParticleOption.CODEC, type -> KhromaParticleOption.STREAM_CODEC));

	/**
	 * 
	 * CREATIVE TAB
	 * 
	 */
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SURGEOFKHROMA_TAB = CREATIVE_MODE_TABS.register("surgeofkhroma_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("itemGroup.surgeofkhroma")).withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> ItemReference.CHROMATIC_NUCLEUS.get().getDefaultInstance()).displayItems(RegistryReference::tabItemsToDisplay).build());

	public static void tabItemsToDisplay(ItemDisplayParameters parameters, Output output) {
		for (var entry : ItemReference.ITEMS.getEntries())
			output.accept(entry.get());
	}

	private static <T extends ParticleOptions> ParticleType<T> registerParticle(boolean overrideLimitter, final Function<ParticleType<T>, MapCodec<T>> codecGetter,
			final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
		return new ParticleType<T>(overrideLimitter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		};
	}
}
