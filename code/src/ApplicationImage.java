import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Loading_view;
import view.Navigator;

/**
 * Classe principal du projet permet de créer la stage du projet avec sa taille et de générer le FXML
 * Etend de la classe Application de java
 * @author Tremblay Jeremy, Vignon Ugo, Viton Antoine, Wissocq Maxime, Coudour Adrien
 */
public class ApplicationImage extends Application {
    /**
     * Redéfinition de la classe Application
     * Définit les paramètres du stage et génère le FXML
     * @param primaryStage Container de java permettant l'affichage
     * @throws Exception si le fxmlLoader ne réussit par à charger le fichier FXML
     */
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
