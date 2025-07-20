package net.deatrathias.khroma.datagen;

import java.util.concurrent.CompletableFuture;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.CuriosResources;

public class CuriosDataGen extends CuriosDataProvider {

	public CuriosDataGen(PackOutput output, CompletableFuture<Provider> registries) {
		super(SurgeofKhroma.MODID, output, registries);
	}

	@Override
	public void generate(Provider registries) {
		createSlot("eyes").order(50).icon(SurgeofKhroma.resource("slot/empty_eyes_slot")).addValidator(CuriosResources.resource("tag"));
		createEntities("player").addPlayer().addSlots("eyes");
	}
}
