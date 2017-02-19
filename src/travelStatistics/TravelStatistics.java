package travelStatistics;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class TravelStatistics extends Application{
	public static Stage primaryStage;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(TravelStatistics.class, (java.lang.String[])null);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		//TODO richtig machen
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane page = (AnchorPane) FXMLLoader.load(TravelStatistics.class.getResource("TravelStatistics.fxml"));
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Travel Statistics");
			primaryStage.show();
		} catch (Exception ex) {
			Logger.getLogger(TravelStatistics.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	

}
