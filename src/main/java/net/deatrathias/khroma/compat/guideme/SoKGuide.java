package net.deatrathias.khroma.compat.guideme;

import guideme.Guide;
import net.deatrathias.khroma.SurgeofKhroma;

public class SoKGuide {

	public static Guide KHROMANCER_ARCHIVE = null;

	public static void create() {
		KHROMANCER_ARCHIVE = Guide.builder(SurgeofKhroma.resource("khromancer_archive")).build();
	}

}
