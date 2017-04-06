package net.sf.mcf2pdf.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.sf.mcf2pdf.gui.Loader;
import net.sf.mcf2pdf.gui.components.LanguageComboBox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tbee.javafx.scene.layout.MigPane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController {
	private static final Log LOG = LogFactory.getLog(HomeController.class);
	@FXML
	private Label convertLabel;
	@FXML
	private MigPane mainPane;
	@FXML
	private LanguageComboBox languageCombo;


	public HomeController(){
		LOG.debug("Home controller creation");
	}

//	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		LOG.debug("Home controller init");
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML " + fxmlFileLocation;
		assert languageCombo != null : "fx:id=\"languageCombo\" was not injected: check your FXML " + fxmlFileLocation;
		assert convertLabel != null : "fx:id=\"convertLabel\" was not injected: check your FXML " + fxmlFileLocation;

	}

	public void changeLanguage(ActionEvent actionEvent) {
		LOG.debug("Change language to " + languageCombo.getSelectionModel().getSelectedItem());
		MigPane relodedPane =  (MigPane)Loader.loadFxml("home",languageCombo.getValue());
		mainPane.getScene().setRoot(relodedPane);
		LanguageComboBox newLanguageComboBox = (LanguageComboBox)relodedPane.lookup("#languageCombo");
		newLanguageComboBox.getSelectionModel().select(languageCombo.getValue());
		LOG.debug("Language changed");
	}
}
