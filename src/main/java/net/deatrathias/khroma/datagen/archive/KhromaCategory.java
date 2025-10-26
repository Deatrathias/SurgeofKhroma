package net.deatrathias.khroma.datagen.archive;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;

import net.deatrathias.khroma.datagen.archive.khroma.WhatIsKhromaEntry;
import net.deatrathias.khroma.registries.ItemReference;

public class KhromaCategory extends CategoryProvider {
	public static final String ID = "khroma";

	public KhromaCategory(ModonomiconProviderBase parent) {
		super(parent);
	}

	@Override
	protected String[] generateEntryMap() {
		return new String[] { "a" };
	}

	@Override
	protected void generateEntries() {
		new WhatIsKhromaEntry(this).generate('a');
	}

	@Override
	protected String categoryName() {
		return "Khroma";
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(ItemReference.CHROMATIC_NUCLEUS);
	}

	@Override
	public String categoryId() {
		return ID;
	}
}
