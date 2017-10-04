package Main; /**
 * Created by GianDavid on 30.04.2017.
 */

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTabPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LightWeightBrowser extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            JFXTabPane jfxTabPane = FXMLLoader.load(getClass().getResource("Display.fxml"));
            JFXDecorator decorator = new JFXDecorator(primaryStage, jfxTabPane);
            decorator.setCustomMaximize(true);
            Scene scene = new Scene(decorator, 800, 850);
            primaryStage.setScene(scene);
            //primaryStage.getIcons().add(new Image("./Resources/Browser.png"));
            primaryStage.setTitle("GD Browser");
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
