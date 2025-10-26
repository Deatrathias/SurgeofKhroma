package net.deatrathias.khroma.datagen;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.datagen.archive.KhromaCategory;
import net.deatrathias.khroma.registries.ItemReference;

public class KhromancerArchiveDataGen extends SingleBookSubProvider {
	public KhromancerArchiveDataGen(ModonomiconLanguageProvider lang) {
		super("khromancer_archive", SurgeofKhroma.MODID, lang);
	}

	@Override
	protected void registerDefaultMacros() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generateCategories() {
		add(new KhromaCategory(this).generate());
	}

	@Override
	protected String bookName() {
		return "Khromancer's Archive";
	}

	@Override
	protected String bookTooltip() {
		return "book tooltip";
	}
	
	@Override
	protected BookModel additionalSetup(BookModel book) {
		return book.withGenerateBookItem(false).withCustomBookItem(ItemReference.KHROMANCER_ARCHIVE.getId());
	}
}
