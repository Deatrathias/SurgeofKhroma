package net.deatrathias.khroma.compat.guideme;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import guideme.Guide;
import guideme.color.ColorValue;
import guideme.color.ConstantColor;
import guideme.color.SymbolicColorResolver;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.resources.ResourceLocation;

public class SoKGuide {

	public static Guide KHROMANCER_ARCHIVE = null;
	
	private static final Map<ResourceLocation, ColorValue> COLOR_MAP = ImmutableMap.of(
			SurgeofKhroma.resource("khroma/red"), new ConstantColor(0xFFFF0000, 0xFFFF0000),
			SurgeofKhroma.resource("khroma/green"), new ConstantColor(0xFF00FF00, 0xFF00FF00)
			);

	public static void create() {
		KHROMANCER_ARCHIVE = Guide.builder(SurgeofKhroma.resource("khromancer_archive")).extension(SymbolicColorResolver.EXTENSION_POINT, COLOR_MAP::get).build();
	}

}
