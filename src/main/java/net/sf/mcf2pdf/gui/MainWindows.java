package net.sf.mcf2pdf.gui;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;

public class MainWindows extends Application {
	private Stage stage;
	
	public static void main(String[] args) {
		System.out.println( "Main method inside Thread : " +  Thread.currentThread().getName());
		LauncherImpl.launchApplication(MainWindows.class, SplashScreen.class, args);
	}

	@Override
	public void init(){
		// Pour voir le SplashScreen
		for (float i=0; i< 10000; i++){
			notifyPreloader(new Preloader.ProgressNotification(i/10000));
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		System.out.println( "Start method inside Thread : " +  Thread.currentThread().getName());
		this.stage = stage;
		stage.setTitle("mcf2pdf Convertor");
		stage.getIcons().add(new Image(new File("src/main/resources/cewe_pdf.ico").toURI().toString()));
		stage.setScene(createScene());
		stage.show();
	}
	
	private Scene createScene() {
		final MigPane container = new MigPane(new LC().wrapAfter(4));

		Button convertBtn = new Button("Convert !");
		convertBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				System.out.println("Clic");
				new OptionalWindows();
			}
		});

		Label scrLbl = new Label("Source");
		final TextField srcField = new TextField();
		srcField.setPromptText("Select the mcf file");
		FileChooser fileChooser = new FileChooser();
		Button openerBtn = new Button("…");
		openerBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				System.out.println("Clic");
				File file = showFileOpener(stage);
				srcField.setText(file.getPath());
			}
		});

		Label dstLbl = new Label("Destination");
		final TextField dstField = new TextField();
		dstField.setPromptText("Select the output file");
		Button saverBtn = new Button("…");
		saverBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				System.out.println("Clic");
				File file = showFileSaver(stage);
				dstField.setText(file.getPath());
			}
		});

		//http://o7planning.org/en/11185/javafx-spinner-tutorial
		Label dpiLbl = new Label("DPI");
		Spinner<Integer> dpiSpn = new Spinner<Integer>();
		dpiSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,600,150, 10));
		dpiSpn.setEditable(true);
		dpiSpn.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

		Label nbPgLbl = new Label("Nb pages");
		Spinner<Integer> nbPgSpn = new Spinner<Integer>();
		nbPgSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,99,0, 1));
		nbPgSpn.setEditable(true);
		nbPgSpn.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);


		container.add(convertBtn, "dock west");

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

		return new Scene(container);
	}

	private File showFileOpener(Stage stage){
		return  showFileChooser(stage, true, "Choose a File", "mcf");
	}

	private File showFileSaver(Stage stage){
		return  showFileChooser(stage, true, "Save File", "pdf");
	}

	private File showFileChooser(Stage stage, boolean saver, String title, String... extentions){
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
