/*
 * Copyright © 2017 Mimoza <github@zhext.tk>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file or www.wtfpl.net
 * for more details.
 */

package net.sf.mcf2pdf.gui.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.mcf2pdf.gui.Loader;
import net.sf.mcf2pdf.gui.i18n.Language;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
	private static final Log LOG = LogFactory.getLog(HomeController.class);
	private Stage stage=null;

	@FXML
	private Spinner pagesSpinner;
	@FXML
	private  Spinner dpiSpinner;

	@FXML
	private Menu aboutMenuItem;
	@FXML
	private Menu langMenu;

	@FXML
	private MigPane mainPane;


	@FXML
	private Button convertButton;

	public HomeController(){
		LOG.debug("Home controller creation");
	}

	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		LOG.debug("Home controller init");
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML " + fxmlFileLocation;
		fillLanguageMenu(resources.getLocale());
	}

	private Stage getStage(){
		if (stage == null){
			if (mainPane.getScene().getWindow() instanceof Stage) {
				stage = (Stage) mainPane.getScene().getWindow();
			}else {
				throw new UnsupportedOperationException("The windows is not a Stage object");
			}
		}
		return stage;
	}

	private void fillLanguageMenu(Locale locale) {
		LOG.debug("Fill the language menu");
		for (Language language : Language.values()){
			MenuItem langItem = new MenuItem(language.getLocale().getDisplayLanguage(locale));

			ImageView imageView = new ImageView(language.getFlag());
			imageView.setPreserveRatio(true);
			imageView.setFitHeight(25.0);
			langItem.setGraphic(imageView);

			langItem.setOnAction(actionEvent -> {
				LOG.debug("Change language to " + language.getLocale());
				MigPane relodedPane =  (MigPane)Loader.loadFxml("home",language);
				mainPane.getScene().setRoot(relodedPane);
			});
			langMenu.getItems().add(langItem);
		}
	}

	public void showAbout(ActionEvent actionEvent) {
		LOG.debug("Showing about dialog");
	}

	public void startConversion(ActionEvent actionEvent) {
		LOG.debug("Starting convertion");
	}

	public void closeAction(){
		LOG.debug("Closing the application");
		mainPane.getScene().getWindow().hide();
		// JavaFx exit
		Platform.exit();
	}

	@FXML
	private void showFileOpener(){
		showFileChooser(true, "Choose a File", "mcf");
	}

	@FXML
	private void showFileSaver(){
		showFileChooser(true, "Save File", "pdf");
	}

	private File showFileChooser(boolean saver, String title, String... extentions){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		for (String extention : extentions){
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extention.toUpperCase(), "*."+extention.toLowerCase()));
		}
		if (saver) {
			return fileChooser.showSaveDialog(getStage());
		}else {
			return fileChooser.showOpenDialog(getStage());
		}
	}

	//http://o7planning.org/en/11185/javafx-spinner-tutorial
	public class ActionSpinner implements ChangeListener<String> {

		private Spinner<Integer> spinner;

		public ActionSpinner(Spinner<Integer> spinner){
			this.spinner = spinner;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			try {
				if (newValue != null && newValue.isEmpty()){
					spinner.getEditor().setText("0");
				}else {
					spinner.getValueFactory().setValue(Integer.parseInt(newValue));
				}

			} catch (NumberFormatException e) {
				spinner.getEditor().setText(oldValue);
				LOG.debug(e);
			}
		}
	}
}
