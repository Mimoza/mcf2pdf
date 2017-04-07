/*
 * Copyright © 2017 Mimoza <github@zhext.tk>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file or www.wtfpl.net
 * for more details.
 */

package net.sf.mcf2pdf.gui.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import net.sf.mcf2pdf.gui.i18n.Language;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LanguageComboBox extends ComboBox<Language>{
	private static final Log LOG = LogFactory.getLog(LanguageComboBox.class);

	public LanguageComboBox(){
		LOG.debug("Language combo box creation");
		ObservableList<Language> options = FXCollections.observableArrayList(Language.values());
		this.setItems(options);
		this.setConverter(new LanguageStringConverter());
		this.getSelectionModel().selectFirst();

		this.setCellFactory(language -> new LanguageListCell());
		this.setButtonCell(new LanguageListCell());
	}

	private static class LanguageStringConverter extends StringConverter<Language> {
		@Override
		public String toString(Language language) {
			return language.getLocale().getDisplayLanguage();
		}

		@Override
		public Language fromString(String string) {
			return null;
		}
	}

	private static class LanguageListCell extends ListCell<Language> {
		@Override
		protected void updateItem(Language language, boolean empty) {
			super.updateItem(language, empty);
			setGraphic(null);
			setText(null);
			setDisable(empty);
			if (language != null) {
				ImageView imageView = new ImageView(language.getFlag());
				imageView.setFitWidth(24);
				imageView.setFitHeight(16);
				setGraphic(imageView);
				setText(language.getLocale().getDisplayLanguage());
			}
		}
	}
}
