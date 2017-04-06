package net.sf.mcf2pdf.gui;

import com.drew.lang.annotations.Nullable;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import net.sf.mcf2pdf.gui.i18n.Language;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class Loader extends Application {
	private static final Log LOG = LogFactory.getLog(Loader.class);
	private Stage stage;
	
	public static void main(String... args) {
		LOG.debug( "Main method inside Thread : " +  Thread.currentThread().getName());
		LauncherImpl.launchApplication(Loader.class, SplashScreen.class, args);
	}

	@Override
	public void init(){
		// Pour voir le SplashScreen
		for (float i=0; i< 1000; i++){
			notifyPreloader(new Preloader.ProgressNotification(i/10000));
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		LOG.debug("Start method inside Thread : " +  Thread.currentThread().getName());
		this.stage = stage;
		stage.setTitle("mcf2pdf Convertor");
		stage.getIcons().add(new Image(new File("src/main/resources/cewe_pdf.png").toURI().toString()));
		stage.setScene(createScene());
		stage.show();
	}

	public static Pane loadFxml(String fxmlName, Language language){
		LOG.debug(String.format("Loading of %s fxml file with %s language",fxmlName, language));
		Pane pane = null;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			// Here, just the resource bundles name is mentioned. You can add support for more languages
			// by adding more properties-files in fxml folder of resources with language-specific endings like
			// "mcf2pdf_fr_FR.properties".
			fxmlLoader.setResources(ResourceBundle.getBundle("i18n.mcf2pdf", language.getLocale()));
			fxmlLoader.setLocation(Loader.class.getClassLoader().getResource("fxml/"+fxmlName+".fxml"));
			pane =  fxmlLoader.load();
		} catch (IOException ex) {
			LOG.error(ex);
		}
		return pane;
	}

	public static Stage loadFxml(@Nullable Stage stage, String fxmlName, Language language){
		Stage returnStage = stage == null? new Stage():stage ;
		returnStage.setScene(new Scene(loadFxml(fxmlName, language)));
		returnStage.show();
		return returnStage;
	}
	
	private Scene createScene() {
		final MigPane container = new MigPane(new LC().wrapAfter(4));

		Button convertBtn = new Button("Convert !");
		convertBtn.setOnAction(actionEvent -> {
			LOG.debug("Clic Optional");
			new OptionalWindows();
		});

		Button fxmlBtn = new Button("Fxml !");
		Stage fmxStage = new Stage();
		fxmlBtn.setOnAction(actionEvent -> {
			LOG.debug("Clic Fxml");
			loadFxml(fmxStage,"home",Language.FR);
		});

		Label scrLbl = new Label("Source");
		final TextField srcField = new TextField();
		srcField.setPromptText("Select the mcf file");
		FileChooser fileChooser = new FileChooser();
		Button openerBtn = new Button("…");
		openerBtn.setOnAction(actionEvent -> {
			LOG.debug("Clic");
			File file = showFileOpener(stage);
			srcField.setText(file.getPath());
		});

		Label dstLbl = new Label("Destination");
		final TextField dstField = new TextField();
		dstField.setPromptText("Select the output file");
		Button saverBtn = new Button("…");
		saverBtn.setOnAction(actionEvent -> {
			LOG.debug("Clic Destination");
			File file = showFileSaver(stage);
			dstField.setText(file.getPath());
		});

		//http://o7planning.org/en/11185/javafx-spinner-tutorial
		Label dpiLbl = new Label("DPI");
		final Spinner<Integer> dpiSpn = new Spinner<>();
		dpiSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,600,150, 10));
		dpiSpn.setEditable(true);
		dpiSpn.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (newValue != null && newValue.isEmpty()){
					dpiSpn.getEditor().setText("0");
				}else {
					dpiSpn.getValueFactory().setValue(Integer.parseInt(newValue));
				}

			} catch (NumberFormatException e) {
				dpiSpn.getEditor().setText(oldValue);
				LOG.debug(e);
			}
		});
		dpiSpn.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

		Label nbPgLbl = new Label("Nb pages");
		final Spinner<Integer> nbPgSpn = new Spinner<>();
		nbPgSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,99,0, 1));
		nbPgSpn.setEditable(true);
		nbPgSpn.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (newValue != null && newValue.isEmpty()){
					nbPgSpn.getEditor().setText("0");
				}else {
					nbPgSpn.getValueFactory().setValue(Integer.parseInt(newValue));
				}

			} catch (NumberFormatException e) {
				nbPgSpn.getEditor().setText(oldValue);
				LOG.debug(e);
			}
		});
		nbPgSpn.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);


		container.add(convertBtn, "dock west");
		container.add(fxmlBtn, "dock east");
//		migNode(Button{text: 'North'},  "dock north"),
//		migNode(Button{text: 'South'},  "dock south"),
//		migNode(Button{text: 'West'},   "dock west"),
//		migNode(Button{text: 'Center'}, "dock center"),
//		migNode(Button{text: 'East'},   "dock east")


		container.add(scrLbl);
		container.add(srcField, "growx");
		container.add(openerBtn);

		container.add(dstLbl);
		container.add(dstField, "growx");
		container.add(saverBtn);

		container.add(dpiLbl);
		container.add(dpiSpn);
		container.add(nbPgLbl);
		container.add(nbPgSpn);


		Tab defaultTab = new Tab();
		defaultTab.setText("Convert");
		defaultTab.setContent(container);
		defaultTab.setClosable(false);

		Tab advanceTab = new Tab();
		advanceTab.setText("Advance");
		advanceTab.setClosable(false);

		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(defaultTab, advanceTab);

		return new Scene(tabPane);
	}

	private static File showFileOpener(Stage stage){
		return  showFileChooser(stage, true, "Choose a File", "mcf");
	}

	private static File showFileSaver(Stage stage){
		return  showFileChooser(stage, true, "Save File", "pdf");
	}

	private static File showFileChooser(Stage stage, boolean saver, String title, String... extentions){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		for (String extention : extentions){
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extention.toUpperCase(), "*."+extention.toLowerCase()));
		}
		if (saver) {
			return fileChooser.showSaveDialog(stage);
		}else {
			return fileChooser.showOpenDialog(stage);
		}
	}
}
