package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import modele.CataloguePage;
import modele.Manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;


public class Loading_view {

    @FXML
    private Pane mainNode;

    @FXML
    public void initialize(){
        try {
            Manager manager = new Manager(new CataloguePage());

            Button btn = new Button("CHARGER TILESETS");
            btn.setPrefSize(200, 50);
            HBox hb = new HBox(btn);
            hb.setPrefSize(935, 565);
            hb.setAlignment(Pos.CENTER);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        List<File> listFile;
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Selection tilesets");
                        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                                new FileChooser.ExtensionFilter("JPG", "*.jpeg", "*.jpeg", "*.jpe", "*.jfif"));
                        listFile = fileChooser.showOpenMultipleDialog(mainNode.getScene().getWindow());
                        if (listFile == null) return;
                        for (File f : listFile) {
                            manager.getListTilesets().add(f);
                        }
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Selection_view.fxml"));
                        Parent pane = fxmlLoader.load();
                        Selection_view controller = fxmlLoader.getController();
                        controller.setManager(manager);
                        controller.initializeSelectionView();
                        Navigator.getScene().setRoot(pane);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mainNode.getChildren().add(hb);
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
