package net.sf.mcf2pdf.gui.i18n;

import javafx.scene.image.Image;

import java.util.Locale;

public enum Language {
	US(Locale.US),
	FR(Locale.FRANCE),
	ES(new Locale("es", "ES")),
	EO(new Locale("eo", "EO"));

	private  Locale locale;
	private Image flag;

	private Language(Locale locale){
		this.locale = locale;
		this.flag = new Image("/images/flags/" + locale + ".png");
	}

	public Locale getLocale(){
		return this.locale;
	}

	public Image getFlag(){
		return this.flag;
	}
}