import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Loading_view;
import view.Navigator;

public class ApplicationImage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(950);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Loading_view.fxml"));
        Parent Pane = fxmlLoader.load();
        Loading_view loading_view = fxmlLoader.getController();
        Scene scene = new Scene(Pane,primaryStage.getWidth(),primaryStage.getHeight());
        Navigator.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
