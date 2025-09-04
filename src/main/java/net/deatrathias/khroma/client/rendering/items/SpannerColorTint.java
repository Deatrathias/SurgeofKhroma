package net.deatrathias.khroma.client.rendering.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.items.SpannerItem.SpannerColorLocation;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record SpannerColorTint(SpannerColorLocation location) implements ItemTintSource {

	public static final MapCodec<SpannerColorTint> CODEC = RecordCodecBuilder
			.mapCodec(app -> app.group(StringRepresentable.fromEnum(SpannerColorLocation::values).fieldOf("location").forGetter(SpannerColorTint::location)).apply(app, SpannerColorTint::new));

	@Override
	public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
		return stack.get(RegistryReference.DATA_COMPONENT_SPANNER_COLORS).getColorFromLocation(location);
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return CODEC;
	}

}
