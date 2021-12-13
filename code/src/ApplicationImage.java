import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationImage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(950);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        Parent loading_view = FXMLLoader.load(getClass().getResource("/FXML/Selection_view.fxml"));
        Scene scene = new Scene(loading_view,primaryStage.getWidth(),primaryStage.getHeight());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
