/*
 * Copyright © 2017 Mimoza <github@zhext.tk>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file or www.wtfpl.net
 * for more details.
 */

package net.sf.mcf2pdf.gui.windows;

import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.mcf2pdf.gui.Loader;
import net.sf.mcf2pdf.gui.i18n.Language;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tbee.javafx.scene.layout.MigPane;

public class Home {
	private static final Log lOG = LogFactory.getLog(Home.class);
	private Stage stage;
	private MigPane pane;


	public Home(){
		this(new Stage());
	}

	public Home(Stage stage){
		this.stage = stage;
		lOG.debug("Creation of Home controllers");
		pane = (MigPane)Loader.loadFxml("home", Language.US);
		Scene scene = new Scene(pane);
		scene.getStylesheets().add("/style/styles.css");
		stage.setScene(scene);
		stage.setTitle("Mcf2Pdf");
		stage.show();
	}
}
