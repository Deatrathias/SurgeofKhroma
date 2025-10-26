package net.deatrathias.khroma.datagen.archive.khroma;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

import net.deatrathias.khroma.registries.ItemReference;

public class WhatIsKhromaEntry extends EntryProvider {
	public static final String ID = "what_is_khroma";

	public WhatIsKhromaEntry(CategoryProviderBase parent) {
		super(parent);
	}

	@Override
	protected void generatePages() {
		page("explanation", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
		pageTitle("Explanation");
		pageText("It's just the way it is");
	}

	@Override
	protected String entryName() {
		return "What Is Khroma?";
	}

	@Override
	protected Pair<Integer, Integer> entryBackground() {
		return Pair.of(0, 0);
	}

	@Override
	protected BookIconModel entryIcon() {
		return BookIconModel.create(ItemReference.CHROMATIC_NUCLEUS);
	}

	@Override
	protected String entryId() {
		return ID;
	}

}
