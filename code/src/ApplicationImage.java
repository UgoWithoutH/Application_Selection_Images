import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ApplicationImage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(false);

        Parent loading_view = FXMLLoader.load(getClass().getResource("/FXML/selection_view.fxml"));
        Scene scene = new Scene(loading_view,primaryStage.getWidth(),primaryStage.getHeight());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
