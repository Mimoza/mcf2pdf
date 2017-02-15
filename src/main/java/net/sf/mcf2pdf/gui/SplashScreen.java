package net.sf.mcf2pdf.gui;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Preloader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

//https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
public class SplashScreen extends Preloader{

	private ProgressBar bar;
	private ImageView imageView;
	private Stage stage;

	public static void main(String[] args) {
		System.out.println( "Main method inside Thread : " +  Thread.currentThread().getName());
		LauncherImpl.launchApplication(MainWindows.class, SplashScreen.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {


		File file = new File("src/main/resources/mcf2pdf.png");
		Image image = new Image(file.toURI().toString());
		imageView = new ImageView(image);

		bar = new ProgressBar();
		bar.setPrefSize(image.getWidth(),20);

		final Pane splashLayout = new VBox();
		splashLayout.getChildren().addAll(imageView, bar);
		final Scene scene = new Scene(splashLayout, image.getWidth(), image.getHeight() + bar.getPrefHeight());


		this.stage = stage;
		stage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		stage.setScene(scene);
		stage.setX(bounds.getMinX() + bounds.getWidth() / 2 - scene.getWidth() / 2);
		stage.setY(bounds.getMinY() + bounds.getHeight() / 2 - scene.getHeight() / 2);
		stage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		System.out.println("State change notification : " + stateChangeNotification);
		if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
			stage.hide();
		}
	}

	@Override
	public void handleProgressNotification(ProgressNotification progressNotification) {
		bar.setProgress(progressNotification.getProgress());
		System.out.println("Progress " + bar.getProgress());
	}

	@Override
	public void handleApplicationNotification(PreloaderNotification arg0) {
		if (arg0 instanceof ProgressNotification) {
			handleProgressNotification((ProgressNotification) arg0);
		}
		else if (arg0 instanceof StateChangeNotification) {
			handleStateChangeNotification((StateChangeNotification) arg0);
		}
		else if (arg0 instanceof ErrorNotification) {
			handleErrorNotification((ErrorNotification) arg0);
		}
	}

	@Override
	public boolean handleErrorNotification(Preloader.ErrorNotification info){
		return false;
	}
}
