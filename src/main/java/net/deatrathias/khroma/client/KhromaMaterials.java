package net.deatrathias.khroma.client;

import java.util.HashMap;
import java.util.Map;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class KhromaMaterials {
	private static final Map<Khroma, Material> materialPerKhroma = new HashMap<Khroma, Material>();
	
	public static ResourceLocation getTexturePerKhroma(Khroma khroma) {
		if (khroma == Khroma.SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum");
		else if (khroma == Khroma.LIGHT_SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum_white");
		else if (khroma == Khroma.DARK_SPECTRUM)
			return SurgeofKhroma.resource("block/khroma_spectrum_black");
		else if (khroma == Khroma.KHROMEGA)
			return SurgeofKhroma.resource("block/khroma_khromega");
		else
			return SurgeofKhroma.resource("block/khroma");
	}
	
	@SuppressWarnings("deprecation")
	public static void initialize() {
		if (!materialPerKhroma.isEmpty())
			return;
		
		for (var khroma : Khroma.allKhroma()) {
			materialPerKhroma.put(khroma, new Material(TextureAtlas.LOCATION_BLOCKS, getTexturePerKhroma(khroma)));
		}
	}
	
	public static Material getMaterial(Khroma khroma) {
		initialize();
		return materialPerKhroma.get(khroma);
	}
}
